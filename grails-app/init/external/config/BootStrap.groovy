package external.config

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
	println "Value of external.config.type: " + grailsApplication.config.getProperty("external.config.type")
    }
    def destroy = {
    }
}
