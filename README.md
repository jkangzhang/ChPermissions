# ChPermissions

ChPermissions helps you with the logic of runtime permissions easily on Android M or higher. After a simple steps of initialization you can easily controll your permissions with annotations and callbacks.

## Before Using

In this library, we defined two types of Permission:
 <b>Static Permission</b> and <b>Runtime Permission</b>.  

A Static Permission means it should be checked as long as the Activity/Fragment is fully loaded; and a Runtime Permission may be checked at any time when the Activity/Fragment is running,  it is often triggered by user's interaction.  

For example, there is a scenario that you have an activity for displaying old photos and taking new photos.  Then you may read data from a sd-card file, you will have to request "READ_ EXTRENAL_ PERMISSION" permission on the activity's "onCreate"  lifecycle callback; and you have to request the "CAMERA" permission as soon as user press a "take photo" button. Then the "READ_ EXTERNAL_ PERMISSION" should be a Static Permission and "CAMERA" should be a Runtime Permission.  

So at the very first of using this library's apis on one of your business you should seperate the StaticPermissions and the Runtime Permissions.
 
## Usage  

Implements PermissionPropose interface in your activity/fragment, to listen the callback of the result of permission request. （We suggest you to do this in your base Activity/Fragment）  

```
public class BaseActivity extends AppCompatActivity implements PermissionProposer {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAllStaticPermissionGranted() {
        
    }
    @Override
    public void onPermissionDenied(String permission) {

    }
    @Override
    public void onPermissionGranted(String permission) {

    }
    @Override
    public PermissionHelper getPermissionHelper() {
        return null;
    }
}
```  

Create a PermissionHelper instance with the activity/fragment.  

```
 protected PermissionHelper mPermissionHelper;
 @Override
 protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper<Fragment>(this);
 }  
 @Override
 public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
 }
```  
Override the onRequestPermissionResult() and pass the result value.  

```  
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onResult(requestCode, permissions, grantResults);
}

```
Ok, all initialization is done. You can start your own bussiness easily.  


Example:  your main activity need "READ_ PHONE_ STATE" and "WRITE_ EXTERNAL_ STORAGE" permissions as its Static Permission and "CAMERA" permission as its Runtime Permission when got a onClick event from a button. You just need to declare it at the top of class like this.  

```
@StaticPermission(permession = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE)
@RuntimePermission(permession = {Manifest.permission.CAMERA}
public class MainActivity extends BaseActivity{
    //NOTE: The ChPermission componant has been initilized in BaseActivity below.
}
```

#### StaticPermission
StaticPermissions you declare by notation will be auto checked when MainActivity is created. When user grants a permission, onPermissionGranted() will be callback; otherwise if user denied a permission, onPermissionDenied() will response to it. You may overide them and do something like calling finish() on the activity when some permissions are denied.  

```
@Override
public void onPermissionGranted(String permission) {
   if(permission.equals(Manifest.permission.READ_PHONE_STATE) {
      // Display the contacts listview
   }
}
@Override
public void onPermissionDenied(String permission) {
    ...
    if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
      finish(); // Stop if there is no proper permission.
    }
}
```  

What's more,  there is also a useful method <b>onAllStaticPermissionGranted()</b> which will be callback when all the static permissions are granted by user, it's really useful because we always take the static permission as a bundle.

```  
@Override
public void onAllStaticPermissionGranted() {
    // Init views which are depended on permissions.
}
``` 

If some of the StaticPermissions is optional (has less effect to render view), you can call ignore() to skip it when it is denied, and you will still receive onAllStaticPermissionGranted().

```
@Override
public void onPermissionDenied(String permission) {
    ...
    if(permission.equals(Manifest.permission.READ_PHONE_STATE) {
        mPermissionHelper.ignore();
    }
}
```

#### RuntimePermissions
RuntimePermissions are also need to be declared on the top of your Activity/Fragment by using 
@RuntimePermission identifier. You can check runtime permissions by its index in the array of the @RuntimePermission annotation at any time you like.

```
@Override
public void onCreate(Bundle saveInstanceState) {
    super.onCreate(saveInstanceState);
    Button btnOpenCamera = (Button)findViewById(R.id.btn_open_camera);
    btnOpenCamera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          mPermissionHelper.checkRuntimePermission(0);
      }
   });
}
```

And the result will also callback in onPermissionGranted() and onPermissionDenied().

```
@Override
public void onPermissionGranted(String permission) {
   ...
   if(permission.equals(Manifest.permission.CAMERA) {
       // Open camera.
   }
}
```

## Important read
Don't initial your fragment in the permission callback, because the change of permission state will trigger the onSaveInstance() callback, this will cause a runtime crash, and Android doesn't allow you to use FragmentTransaction's commit method after that.