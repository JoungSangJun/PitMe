package ac.baekseok.pitmeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private BottomNavigationView bottomNavigationView; //?????? ??? ???
    private DrawerLayout drawerLayout; //?????????
    private View drawerView;
    private ImageView iv_person; //?????? ???????????????
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://pitmeproject.appspot.com"); //firebase storage ??????
    private ImageView imgTop;
    private ImageView imgBottom;
    private Button btnRefresh;//??????????????????
    private ImageView imgModel; //???????????????
    private Switch swBlackMode; //???????????????
    private Button btnSaveImage;
    private FrameLayout frameLayout;
    private float previous_top_x = 0; //?????? ????????? ??? x???
    private float after_top_x = 2; //?????? ?????? ??? x???
    private float previous_top_y = 0; //?????? ????????? ??? y???
    private float after_top_y = 2;  //?????? ?????? ??? y???
    private float previous_bottom_x = 0; //?????? ????????? ??? x???
    private float after_bottom_x = 2; //?????? ?????? ??? x???
    private float previous_bottom_y = 0; //?????? ????????? ??? y???
    private float after_bottom_y = 2; //?????? ?????? ??? y???
    private boolean cloth_select; //true?????? ??????, false?????? ????????? ??????
    private DrawerLayout bottom_drawer;
    private View drawer_bottomView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //???????????? ?????? ??????
        drawerView = (View) findViewById(R.id.drawer); //????????????
        Button btn_open = (Button) findViewById(R.id.btn_open);//?????? ??????
        iv_person = (ImageView) findViewById(R.id.iv_person); //??????????????????
        StorageReference storageReference = storage.getReference(); //firebase??? ???????????? ??????
        Intent intent = getIntent(); //????????? ??? email ?????? ?????? intent
        String email = intent.getStringExtra("email"); //LoginActivity?????? email ??? ?????????
        imgTop = (ImageView) findViewById(R.id.imgTop);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);//??????????????????
        imgModel = (ImageView) findViewById(R.id.imgModel);
        swBlackMode = (Switch) findViewById(R.id.swBlackMode); //?????? ??????????????? ?????? ?????????
        btnSaveImage = (Button) findViewById(R.id.btnSaveImg); //????????? ?????? ?????? ??????
        frameLayout = (FrameLayout) findViewById(R.id.main_frame);
        bottom_drawer = (DrawerLayout) findViewById(R.id.bottom_drawer); //?????? ?????????
        drawer_bottomView = (View) findViewById(R.id.bottom_drawer1);
        Button btn_TopMenuOpen = (Button) findViewById(R.id.btn_TopMenuOpen); //?????? ?????? ????????????
        Button btn_BottomMenuOpen = (Button) findViewById(R.id.btn_BottomMenuOpen); //?????? ?????? ????????????
        /* ??? ?????? ?????? ??????*/
        Button btn_Up = (Button) findViewById(R.id.btn_Up);
        Button btn_Down = (Button) findViewById(R.id.btn_Down);
        Button btn_Left = (Button) findViewById(R.id.btn_Left);
        Button btn_Right = (Button) findViewById(R.id.btn_Right);
        /* ??? ?????? ?????? ??????*/
        Button btn_length_up = (Button) findViewById(R.id.btn_length_up);
        Button btn_length_down = (Button) findViewById(R.id.btn_length_down);
        Button btn_width_up = (Button) findViewById(R.id.btn_width_up);
        Button btn_width_down = (Button) findViewById(R.id.btn_width_down);


        //?????? ??? ?????? ??????
        btn_length_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().height = imgTop.getHeight() + 3; //?????? ?????? ??????
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                } else {
                    imgBottom.getLayoutParams().height = imgBottom.getHeight() + 3; //?????? ?????? ??????
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                }

            }
        });


        //?????? ??? ?????? ??????
        btn_length_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().height = imgTop.getHeight() - 3; //?????? ?????? ??????
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                } else {
                    imgBottom.getLayoutParams().height = imgBottom.getHeight() - 3; //?????? ?????? ??????
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                }

            }
        });

        //?????? ??? ?????? ??????
        btn_width_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().width = imgTop.getWidth() + 3; //?????? ?????? ??????
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                } else {
                    imgBottom.getLayoutParams().width = imgBottom.getWidth() + 3; //?????? ?????? ??????
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                }

            }
        });

        //?????? ??? ?????? ??????
        btn_width_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().width = imgTop.getWidth() - 3; //?????? ?????? ??????
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                } else {
                    imgBottom.getLayoutParams().width = imgBottom.getWidth() - 3; //?????? ?????? ??????
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //????????? ?????? ????????? ??????, ???????????? ????????? ????????? ??????
                }

            }
        });


        //??? ?????? ?????? ?????????
        Button btn_close = (Button) findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.closeDrawers();
            }
        });

        //????????? ????????? ?????????
        btn_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????
                if (cloth_select == true) {
                    ObjectAnimator TopX = ObjectAnimator.ofFloat(imgTop, "translationX",
                            previous_top_x, after_top_x);

                    TopX.start();
                    previous_top_x += 2;
                    after_top_x += 2;
                }
                //??????
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationX",
                            previous_bottom_x, after_bottom_x);

                    BottomX.start();
                    previous_bottom_x += 2;
                    after_bottom_x += 2;
                }
            }

        });
        //?????? ????????? ?????????
        btn_Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????
                if (cloth_select == true) {
                    ObjectAnimator TopX = ObjectAnimator.ofFloat(imgTop, "translationX",
                            previous_top_x, after_top_x);

                    TopX.start();
                    previous_top_x -= 2;
                    after_top_x -= 2;
                }
                //??????
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationX",
                            previous_bottom_x, after_bottom_x);

                    BottomX.start();
                    previous_bottom_x -= 2;
                    after_bottom_x -= 2;
                }

            }
        });
        //?????? ????????? ?????????
        btn_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????
                if (cloth_select == true) {
                    ObjectAnimator TopY = ObjectAnimator.ofFloat(imgTop, "translationY",
                            previous_top_y, after_top_y);

                    TopY.start();
                    previous_top_y += 2;
                    after_top_y += 2;
                }
                //??????
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationY",
                            previous_bottom_y, after_bottom_y);

                    BottomX.start();
                    previous_bottom_y += 2;
                    after_bottom_y += 2;
                }
            }
        });

        //??? ????????? ?????????
        btn_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????
                if (cloth_select == true) {
                    ObjectAnimator TopY = ObjectAnimator.ofFloat(imgTop, "translationY",
                            previous_top_y, after_top_y);

                    TopY.start();
                    previous_top_y -= 2;
                    after_top_y -= 2;
                }
                //??????
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationY",
                            previous_bottom_y, after_bottom_y);

                    BottomX.start();
                    previous_bottom_y -= 2;
                    after_bottom_y -= 2;
                }
            }
        });

        //?????? ?????? ?????? ??????
        btn_TopMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.openDrawer(drawer_bottomView);
                cloth_select = true; //?????? ?????????
            }
        });

        //?????? ?????? ?????? ??????
        btn_BottomMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.openDrawer(drawer_bottomView);
                cloth_select = false; //?????? ?????????
            }
        });

        drawer_bottomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //???,???,???,?????? ?????? ??????
        Date time = new Date(); //????????? ?????? ????????? ?????? ????????? ????????????
        String current_time = sdf.format(time); //String??? ????????? ??????

        //???????????? ?????? ?????????
        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request_Capture(frameLayout, current_time + "_capture");
            }
        });


        //????????? ????????? ???????????? ????????? ??????
        swBlackMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                    imgModel.setImageResource(R.drawable.model_black);
                else
                    imgModel.setImageResource(R.drawable.model_white);
            }
        });

        //?????????????????? ?????????
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebaseStorage ?????? ?????? ????????????
                storageReference.child(email).child("Top").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgTop);
                    }
                });

                //firebaseStorage ?????? ?????? ????????????
                storageReference.child(email).child("Bottom").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgBottom);
                    }
                });
            }
        });

        //firebaseStorage ?????? ?????? ????????????
        storageReference.child(email).child("Top").getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgTop);
                    }
                });

        //firebaseStorage ?????? ?????? ????????????
        storageReference.child(email).child("Bottom").getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgBottom);
                    }
                });

        //???????????? ?????????
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
                //?????????????????? ????????? ????????????(?????? ????????? ????????? ?????? ?????? ????????? ?????? ??????)
                storageReference.child(email).child("Profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    //firebase storage??? ?????? ???????????? ???????????? ???????????? ??????????????? ?????????
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).circleCrop().into(iv_person);
                    }
                });
            }
        });


        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        TextView tv_close = (TextView) findViewById(R.id.tv_close);
        //?????? ?????? ?????? ?????????
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        /*????????? ?????? ?????? ?????????*/
        TextView tv_profileEdit = (TextView) findViewById(R.id.tv_profileEdit);

        tv_profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("email", email); //????????? ??? ??? ????????? email ??????
                drawerLayout.closeDrawers();
                startActivity(intent);
            }
        });

        /*??????????????? ?????? ?????????*/
        TextView tv_explain = (TextView) findViewById(R.id.tv_explain);

        tv_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AnnounceActivity.class);
                drawerLayout.closeDrawers();
                startActivity(intent);
            }
        });

        /*?????????????????????*/
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_cloth:
                        setFrag(1);
                        break;
                }
                return true;
            }
        });

        /*????????????*/
        mFirebaseAuth = FirebaseAuth.getInstance();

        TextView tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //???????????? ??????
                mFirebaseAuth.signOut();
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate()


    public void Request_Capture(View view, String title) {
        if (view == null) { //Null Point Exception ERROR ??????
            System.out.println("????????? ??????");
            return;
        }

        /* ?????? ?????? ?????? */
        view.buildDrawingCache(); //?????? ?????? ??? ?????????
        Bitmap bitmap = view.getDrawingCache();
        FileOutputStream fos;

        /* ????????? ?????? Setting */
        File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/"); //?????? ?????? (File Type??? ??????)


        if (!uploadFolder.exists()) { //?????? ????????? ????????? ?????????
            uploadFolder.mkdir(); //?????? ??????
        }

        /* ?????? ?????? */
        String Str_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/"; //?????? ?????? (String Type ??????)

        try {
            fos = new FileOutputStream(Str_Path + title + ".jpg"); // ?????? + ?????? + .jpg??? FileOutputStream Setting
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            Toast.makeText(MainActivity.this, "???????????? ??????", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//Request_Capture


    //??????????????? ?????? ?????????
    private void setFrag(int n) {
        switch (n) {
            case 0:
                break;
            case 1:
                Intent intent = getIntent(); //????????? ??? email ?????? ?????? intent
                String email = intent.getStringExtra("email"); //LoginActivity?????? email ??? ?????????
                Intent intent2 = new Intent(MainActivity.this, Cloth.class);
                intent2.putExtra("email", email);
                startActivity(intent2);
                break;
        }
    }
}//MainActivity`