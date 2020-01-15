package com.plugin.example;

import com.dtolabs.rundeck.core.execution.workflow.steps.FailureReason;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.jobs.JobLifecycleStatus;
import com.dtolabs.rundeck.core.jobs.JobLifecycleStatusImpl;
import com.dtolabs.rundeck.core.jobs.JobPersistEvent;
import com.dtolabs.rundeck.core.jobs.JobPreExecutionEvent;
import com.dtolabs.rundeck.core.plugins.JobLifecyclePluginException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.project.JobLifecyclePlugin;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.step.StepPlugin;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;
import com.dtolabs.rundeck.plugins.util.PropertyBuilder;
import com.dtolabs.rundeck.plugins.PluginLogger;
import org.rundeck.client.RundeckClient;
import org.rundeck.client.api.RundeckApi;
import org.rundeck.client.api.model.ProjectConfig;
import org.rundeck.client.util.Client;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

@Plugin(service=ServiceNameConstants.JobLifecycle,name=JobLifeCycleApiExample.SERVICE_PROVIDER_NAME)
@PluginDescription(title="Job LifeCycle Example", description="Checking if project setting exists")
public class JobLifeCycleApiExample implements JobLifecyclePlugin{

    public static final String SERVICE_PROVIDER_NAME = "job-lifecycle-api-example";

    @PluginProperty(title = "API URL",
            description = "Rundeck API URL",
            required = true,
            scope = PropertyScope.Project)
    String baseUrl;

    @PluginProperty(title = "API Token",
            description = "Rundeck API Token",
            required = true,
            scope = PropertyScope.Project)
    String token;

    @PluginProperty(title = "Required Project Setting",
            description = "Required Project Setting Name",
            required = true,
            scope = PropertyScope.Project)
    String projectSettingName;

    @Override
    public JobLifecycleStatus beforeJobExecution(JobPreExecutionEvent event) throws JobLifecyclePluginException {

         try {

             System.out.println(String.format(
                     "Base Url %s",
                     baseUrl
             ));

             System.out.println(String.format(
                     "token %s",
                     token
             ));

             System.out.println(String.format(
                     "projectStting name %s",
                     projectSettingName
             ));

             Client<RundeckApi> client = RundeckClient.builder().baseUrl(baseUrl).tokenAuth(token).build();
             Call<ProjectConfig> projectSettingsCall = client.getService().getProjectConfiguration(event.getProjectName());
             Response<ProjectConfig> projectSettingsReponse = projectSettingsCall.execute();

             Boolean status = projectSettingsReponse.isSuccessful();
             System.out.println(String.format(
                     "status %s",
                     status.toString()
             ));

            if(status){
                ProjectConfig projectConfig = projectSettingsReponse.body();
                Map<String, String> projectSettings = projectConfig.getConfig();
                System.out.println(projectSettings.toString());

                if (!projectSettings.containsKey(projectSettingName)) {
                    System.out.println("Property doesnt exists!!!!");

                    return JobLifecycleStatusImpl.builder().successful(false).errorMessage(String.format(
                            "Project setting %s not found",
                            projectSettingName
                    )).build();

                }
            }
        } catch (Exception e) {
             e.printStackTrace();
        }

        return null;
    }

    @Override
    public JobLifecycleStatus beforeSaveJob(JobPersistEvent event) throws JobLifecyclePluginException {
        return null;
    }
}
