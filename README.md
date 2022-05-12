# react-native-start-service

## Getting started

`$ npm install react-native-start-service --save`

### Mostly automatic installation

`$ react-native link react-native-start-service`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`

  - Add `import com.secullum.RNStartServicePackage;` to the imports at the top of the file
  - Add `new RNStartServicePackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:

  ```
  include ':react-native-start-service'
  project(':react-native-start-service').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-start-service/android')
  ```

3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

  ```
      compile project(':react-native-start-service')
  ```

## Usage

```javascript
import RNStartService from 'react-native-start-service';

const result = await RNStartService.startAsync(
  'my.app',
  'my.app.MyService',
  'my.app.MY_ACTION',
  { myParam: 'myValue' }
);

const isInstalled = await RNStartService.isPackageInstalledAsync('my.app');
```
