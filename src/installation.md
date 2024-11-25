# Installing Expressway

### Repositories
Ensure this block is in the "repositories" block of your TeamCode module build.gradle.
If you use any Dairy libraries, this will already be present, so you do not have to add it twice.
```groovy
maven {
    url = 'https://repo.dairy.foundation/releases'
}
```

### Dependencies
Add the following lines to the "dependencies" block of that same build.gradle:
```groovy
implementation "page.j5155.roadrunner:actionadditions:0.1.0"
implementation "page.j5155.roadrunner.actionadditions:ftc:0.1.0"
```