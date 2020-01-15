package com.plugin.joblifecycleapiexample

import com.dtolabs.rundeck.core.jobs.JobPreExecutionEvent
import com.dtolabs.rundeck.plugins.step.PluginStepContext
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException
import com.dtolabs.rundeck.plugins.PluginLogger
import com.plugin.example.JobLifeCycleApiExample
import spock.lang.Specification

class JobLifeCycleApiExampleSpec extends Specification {


    def "check Boolean parameter"(){

        given:

        def example = new JobLifeCycleApiExample()
        def config = Mock(JobPreExecutionEvent)

        when:
        def result = example.beforeJobExecution(config)

        then:
        result == null
    }



}
