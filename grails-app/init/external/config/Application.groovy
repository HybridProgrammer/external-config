package external.config

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    /**
     * Set the {@code Environment} that this object runs in.
     */
    @Override
    void setEnvironment(Environment environment) {
        String configPath = System.properties["appname.config.location"]
        if (!configPath) {
            log.error("appname.config.location is not set.")
            return
        }

        def file = new File(configPath)
        if (!file.exists()) {
            log.error(configPath + " file does not exist. Unable to load external config.")
            return
        }

        if (configPath.endsWith(".yml")) {
            Resource resourceConfig = new FileSystemResource(configPath)
            YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()
            ypfb.setResources(resourceConfig)
            ypfb.afterPropertiesSet()
            Properties properties = ypfb.getObject()
            environment.propertySources.addFirst(new PropertiesPropertySource("appname.config.location", properties))
        } else if (configPath.endsWith(".groovy")) {
            def config = new ConfigSlurper().parse(file.toURI().toURL())
            environment.propertySources.addFirst(new MapPropertySource("CustomConfig", config))
        } else {
            println "Unable to determin the kind of config file. It should end with .yml or .groovy: " + configPath
            return
        }

        println "Loadded config from " + configPath

    }
}
