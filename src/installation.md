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
implementation "page.j5155.roadrunner.expressway:core:0.3.7"
implementation "page.j5155.roadrunner.expressway:ftc:0.3.7"
```

You must do a Gradle Sync after adding these lines to your build.gradle file
so that Android Studio can download the necessary files.

## Updating Expressway

To update Expressway, change the version number in the dependencies block of your build.gradle file to the latest version.