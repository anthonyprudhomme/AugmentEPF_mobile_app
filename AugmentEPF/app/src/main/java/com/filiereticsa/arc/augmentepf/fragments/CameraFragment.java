package com.filiereticsa.arc.augmentepf.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.DestinationSelectedInterface;
import com.filiereticsa.arc.augmentepf.localization.FloorAccess;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMap;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTrackerObserver;
import com.filiereticsa.arc.augmentepf.localization.guidage.Guidance;
import com.filiereticsa.arc.augmentepf.localization.guidage.TrajectorySegment;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.views.GuidanceView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ARC team for AugmentEPF prokect on 14/05/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)

public class CameraFragment extends Fragment
        implements GAFrameworkUserTrackerObserver, DestinationSelectedInterface {

    // constants
    private static final String TAG = "Ici";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    public static DestinationSelectedInterface destinationSelectedInterface;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private FrameLayout frameLayout;
    private int currentCamera = 0;
    private TextureView textureView;
    private String cameraId;
    private Size imageDimension;

    private boolean surfaceTextureAvailable;
    private boolean permissionsGranted;
    private boolean cameraOpened;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_android_camera_api, container, false);
        textureView = (TextureView) view.findViewById(R.id.texture);
        permissionsGranted = hasCameraPermission();
        //startBackgroundThread();
        if (!permissionsGranted) {
            askForPermission();
        }
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        frameLayout = (FrameLayout) view.findViewById(R.id.camera_layout);
        GAFrameworkUserTracker.sharedTracker().registerObserver(this);
        destinationSelectedInterface = this;
        return view;
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

    }

    public void askForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS,
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_CAMERA_PERMISSION);
                return;
            } else {
                surfaceTextureAvailable = true;
                setupCameraIfPossible();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            surfaceTextureAvailable = false;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void setupCameraIfPossible() {
        if (!cameraOpened && surfaceTextureAvailable && permissionsGranted) {
            openCamera(currentCamera);
        }
    }

    private ImageReader imageReader;
    private Handler backgroundHandler;
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    private HandlerThread backgroundThread;
    private GABeaconMap currentMap;
    private GuidanceView guidanceView;
    private Guidance guidance;
    private int index;
    private Pair<Integer, Integer> currentPosition;

    protected void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(
                    Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            //The camera is already closed
                            if (null == cameraDevice) {
                                return;
                            }
                            // When the session is ready, we start displaying the preview.
                            cameraCaptureSessions = cameraCaptureSession;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int cameraSelected) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager manager
                    = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraId = manager.getCameraIdList()[cameraSelected];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map
                        = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                // Add permission for camera and let user grant the permission
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA
                            },
                            REQUEST_CAMERA_PERMISSION);
                    return;
                }
                manager.openCamera(cameraId, stateCallback, null);
                cameraOpened = true;


            } catch (
                    CameraAccessException e)

            {
                e.printStackTrace();
            }
        }

    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),
                    null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
            cameraOpened = false;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                        getContext(),
                        R.string.permission_not_granted,
                        Toast.LENGTH_LONG).show();
            } else {
                permissionsGranted = true;
                setupCameraIfPossible();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            setupCameraIfPossible();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupCameraIfPossible();
    }

    @Override
    public void onStop() {
        super.onStop();
        closeCamera();
    }

    @Override
    public void userMovedToMap(final GABeaconMap map) {
        if (map != null) {
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentMap = map;
                        if (guidanceView == null) {
                            guidanceView = new GuidanceView(
                                    getContext(),
                                    AppUtils.screenHeight,
                                    AppUtils.screenWidth);
                            LinearLayout.LayoutParams userAndPathLayoutParams
                                    = new LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            guidanceView.requestLayout();
                            userAndPathLayoutParams.width = AppUtils.screenWidth;
                            userAndPathLayoutParams.height = AppUtils.screenHeight;
                            guidanceView.setLayoutParams(userAndPathLayoutParams);
                            if (guidanceView.getParent() != null) {
                                ((ViewGroup) guidanceView
                                        .getParent())
                                        .removeView(guidanceView);
                            }
                            if (guidanceView.getParent() != null) {
                                ((ViewGroup) guidanceView
                                        .getParent())
                                        .removeView(guidanceView);
                            }
                            frameLayout.addView(guidanceView);
                        }
                        guidanceView.setFloor(String.valueOf(map.getFloor()));
                    }
                });
            }

        }
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> position,
                                     double heading,
                                     double currentHeading,
                                     String direction) {
        currentPosition = position;
        if (guidanceView != null) {
            guidanceView.setCurrentHeading(heading);
            ArrayList<Place> availableClassRooms = ClassRoom.getAvailableClassroomList();
            if (availableClassRooms != null && availableClassRooms.size() > 0) {
                guidanceView.setClosestRoom(availableClassRooms.get(0).getName());
            }
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        guidanceView.invalidate();
                    }
                });
            }
        }
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> indexPath,
                                     ArrayList<Pair<Integer, Integer>> candidates) {

    }

    @Override
    public void userChangedDirection(Pair<Integer, Integer> newDirection) {

    }

    @Override
    public void onPathChanged(
            Pair<ArrayList<Pair<Integer, Integer>>, Integer> path,
            FloorAccess.FloorAccessType floorAccessType) {
        if (guidance == null && path != null) {
            guidance = new Guidance(path.first);
        }
        if (guidance != null && path == null) {
            guidance.setPath(null);
        }

        // There is a trajectory with instructions and it's not finished yet
        if (guidance != null && index != Integer.MAX_VALUE && guidanceView != null) {
            ArrayList<TrajectorySegment> trajectory = guidance.getTrajectory();

            // Get the index in the segment which correspond at the position
            index = guidance.getCurrentSegment(currentPosition, index);
            if (index == -1) { // Error with the position
                index = 0;
                if (path != null) { // There is a path defined previously
                    guidance.setPath(path.first);
                }
            } else if (index == Integer.MAX_VALUE) { // The end of the path
                guidanceView.setInstruction("Congrats, you reached your destination!");
                guidanceView.setTargetHeading(null);
            } else {
                if (trajectory != null) {
                    guidanceView.setInstruction(trajectory.get(index).getDirectionInstruction());
                    guidanceView.setTargetHeading(trajectory.get(index).getNewDirectionCoordinates());
                    Log.d(TAG, "onPathChanged: " + trajectory.get(index).getDirectionInstruction());
                }
            }
        }

    }

    @Override
    public void onOrientationChange(double currentHeading) {
        if (guidanceView != null) {
            guidanceView.setCurrentHeading(currentHeading);
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        guidanceView.invalidate();
                    }
                });
            }
        }
    }

    @Override
    public void onDestinationSelected(Place place) {
        if (guidanceView != null) {
            guidanceView.setDestination(place.getName());
        }
    }
}