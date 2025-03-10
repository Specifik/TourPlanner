# Media-Library Sample
Demo-Project for BIF4-SWEN2 in Java.

Shows:  
* Main- and Sub-FXML files
* Main- and Sub-ViewModels
* FXMLInjection
* Module Based Project implementation

## Best Practices
1. Split „big“ FXML-files into a MainWindow.fxml including sub-FXML-files using <fx:include> directive
  * Code organization / maintainability
  * Code-reusage
2. One ViewModel-class („Sub-ViewModel“) per FXML-file
3. Use dependency-injection to create controllers and viewmodels

## FXML Dependency Injection
* Dependency Injection Strategies:
  * Constructor injection
  * Field injection
* --> Inversion of control
* Prepare the FXMLLoader to populate Controllers by itself
* Use a ControllerFactory
* The ControllerFactory will connect the Controllers with the corresponding ViewModels

see https://edencoding.com/dependency-injection/

## Module-based Projects with Maven
### Hint 1: Multi-module projects
When generating (or changing to a) a multi-module project you need to run 
```mvn clean install``` in order to update the internal maven-repo of all modules.
Otherwise it may not find the artifacts of other modules.
see (https://books.sonatype.com/mvnex-book/reference/multimodule-sect-intro.html)

### Hint 2: Module dependencies
Don't place java-sources in the parent-project which depends on some of the child modules,
because the parent-project is always build at first!  
Instead create for this code an own child-module.

The build order of the modules is automatically detected by Maven regarding to the module dependencies.
