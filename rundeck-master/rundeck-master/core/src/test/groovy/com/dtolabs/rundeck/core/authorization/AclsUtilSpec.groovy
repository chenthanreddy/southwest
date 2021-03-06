/*
 * Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtolabs.rundeck.core.authorization

import spock.lang.Specification

/**
 * Created by greg on 7/28/15.
 */
class AclsUtilSpec extends Specification {

    def "get groups"(){
        given:
        def builder1 = AclRuleBuilder.builder().with {
            group "monkey"
            resourceType "type"
            allowActions(["a"] as Set)
            sourceIdentity "test1"
            description "blah"
            environment BasicEnvironmentalContext.staticContextFor("project", "project1")
        }
        def rule1 = builder1.build()
        def rule2 = AclRuleBuilder.builder(builder1).with{
            group "monkey2"
            resourceType "typeB"
            build()
        }
        def rule3 = AclRuleBuilder.builder(builder1).with{
            group "monkey"
            resourceType "typeC"
            build()
        }
        def ruleset = new AclRuleSetImpl([rule1,rule2,rule3] as Set)
        when:
        def groups = AclsUtil.getGroups(AclsUtil.source(ruleset))

        then:
        groups==['monkey','monkey2'] as Set
    }

    def "merge sources"(){
        given:
        def builder1 = AclRuleBuilder.builder().with {
            group "monkey"
            resourceType "type"
            allowActions(["a"] as Set)
            sourceIdentity "test1"
            description "blah"
            environment BasicEnvironmentalContext.staticContextFor("project", "project1")
        }
        def rule1 = builder1.build()
        def rule2 = AclRuleBuilder.builder(builder1).with{
            group "monkey2"
            resourceType "typeB"
            build()
        }
        def rule3 = AclRuleBuilder.builder(builder1).with{
            group "monkey"
            resourceType "typeC"
            build()
        }
        def rule4 = AclRuleBuilder.builder(builder1).with{
            group null
            username "elf"
            resourceType "typeC"
            resource( [big:"deal"])
            regexMatch true
            build()
        }
        def ruleset1 = new AclRuleSetImpl([rule1,rule2] as Set)
        def ruleset2 = new AclRuleSetImpl([rule3,rule4] as Set)
        def source1 = AclsUtil.source(ruleset1)
        def source2 = AclsUtil.source(ruleset2)

        when:
        def merged = AclsUtil.merge(source1,source2)

        then:
        merged.ruleSet.rules == [rule1,rule2,rule3,rule4] as Set
    }
}
