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
    private BottomNavigationView bottomNavigationView; //바텀 앱 바
    private DrawerLayout drawerLayout; //메뉴바
    private View drawerView;
    private ImageView iv_person; //메뉴 프로필사진
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://pitmeproject.appspot.com"); //firebase storage 연결
    private ImageView imgTop;
    private ImageView imgBottom;
    private Button btnRefresh;//새로고침버튼
    private ImageView imgModel; //마네킹사진
    private Switch swBlackMode; //스위치버튼
    private Button btnSaveImage;
    private FrameLayout frameLayout;
    private float previous_top_x = 0; //상의 옮기기 전 x축
    private float after_top_x = 2; //상의 옮긴 후 x축
    private float previous_top_y = 0; //상의 옮기기 전 y축
    private float after_top_y = 2;  //상의 옮긴 후 y축
    private float previous_bottom_x = 0; //하의 옮기기 전 x축
    private float after_bottom_x = 2; //하의 옮긴 후 x축
    private float previous_bottom_y = 0; //하의 옮기기 전 y축
    private float after_bottom_y = 2; //하의 옮긴 후 y축
    private boolean cloth_select; //true이면 상의, false이면 하의가 이동
    private DrawerLayout bottom_drawer;
    private View drawer_bottomView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //메뉴화면 나올 자리
        drawerView = (View) findViewById(R.id.drawer); //메뉴화면
        Button btn_open = (Button) findViewById(R.id.btn_open);//메뉴 버튼
        iv_person = (ImageView) findViewById(R.id.iv_person); //프로필이미지
        StorageReference storageReference = storage.getReference(); //firebase에 저장하기 위한
        Intent intent = getIntent(); //로그인 한 email 받기 위한 intent
        String email = intent.getStringExtra("email"); //LoginActivity에서 email 값 받아옴
        imgTop = (ImageView) findViewById(R.id.imgTop);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);//새로고침버튼
        imgModel = (ImageView) findViewById(R.id.imgModel);
        swBlackMode = (Switch) findViewById(R.id.swBlackMode); //배경 검정색으로 하는 스위치
        btnSaveImage = (Button) findViewById(R.id.btnSaveImg); //갤러리 사진 저장 버튼
        frameLayout = (FrameLayout) findViewById(R.id.main_frame);
        bottom_drawer = (DrawerLayout) findViewById(R.id.bottom_drawer); //하단 메뉴바
        drawer_bottomView = (View) findViewById(R.id.bottom_drawer1);
        Button btn_TopMenuOpen = (Button) findViewById(R.id.btn_TopMenuOpen); //상의 이동 메뉴버튼
        Button btn_BottomMenuOpen = (Button) findViewById(R.id.btn_BottomMenuOpen); //하의 이동 메뉴버튼
        /* 옷 위치 변경 버튼*/
        Button btn_Up = (Button) findViewById(R.id.btn_Up);
        Button btn_Down = (Button) findViewById(R.id.btn_Down);
        Button btn_Left = (Button) findViewById(R.id.btn_Left);
        Button btn_Right = (Button) findViewById(R.id.btn_Right);
        /* 옷 크기 변경 버튼*/
        Button btn_length_up = (Button) findViewById(R.id.btn_length_up);
        Button btn_length_down = (Button) findViewById(R.id.btn_length_down);
        Button btn_width_up = (Button) findViewById(R.id.btn_width_up);
        Button btn_width_down = (Button) findViewById(R.id.btn_width_down);


        //길이 ↑ 버튼 클릭
        btn_length_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().height = imgTop.getHeight() + 3; //상의 길이 증가
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                } else {
                    imgBottom.getLayoutParams().height = imgBottom.getHeight() + 3; //하의 길이 증가
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                }

            }
        });


        //길이 ↓ 버튼 클릭
        btn_length_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().height = imgTop.getHeight() - 3; //상의 길이 감소
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                } else {
                    imgBottom.getLayoutParams().height = imgBottom.getHeight() - 3; //하의 길이 감소
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                }

            }
        });

        //넓이 ↑ 버튼 클릭
        btn_width_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().width = imgTop.getWidth() + 3; //상의 넓이 증가
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                } else {
                    imgBottom.getLayoutParams().width = imgBottom.getWidth() + 3; //하의 넓이 증가
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                }

            }
        });

        //넓이 ↓ 버튼 클릭
        btn_width_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloth_select == true) {
                    imgTop.getLayoutParams().width = imgTop.getWidth() - 3; //상의 넓이 감소
                    imgTop.requestLayout();
                    imgTop.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                } else {
                    imgBottom.getLayoutParams().width = imgBottom.getWidth() - 3; //하의 넓이 감소
                    imgBottom.requestLayout();
                    imgBottom.setScaleType(ImageView.ScaleType.FIT_XY); //길이만 증가 시키기 위함, 자동비율 맞춰져 넓이도 커짐
                }

            }
        });


        //옷 이동 메뉴 닫기기
        Button btn_close = (Button) findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.closeDrawers();
            }
        });

        //오른쪽 화살표 클릭시
        btn_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //상의
                if (cloth_select == true) {
                    ObjectAnimator TopX = ObjectAnimator.ofFloat(imgTop, "translationX",
                            previous_top_x, after_top_x);

                    TopX.start();
                    previous_top_x += 2;
                    after_top_x += 2;
                }
                //하의
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationX",
                            previous_bottom_x, after_bottom_x);

                    BottomX.start();
                    previous_bottom_x += 2;
                    after_bottom_x += 2;
                }
            }

        });
        //왼쪽 화살표 클릭시
        btn_Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //상의
                if (cloth_select == true) {
                    ObjectAnimator TopX = ObjectAnimator.ofFloat(imgTop, "translationX",
                            previous_top_x, after_top_x);

                    TopX.start();
                    previous_top_x -= 2;
                    after_top_x -= 2;
                }
                //하의
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationX",
                            previous_bottom_x, after_bottom_x);

                    BottomX.start();
                    previous_bottom_x -= 2;
                    after_bottom_x -= 2;
                }

            }
        });
        //아래 화살표 클릭시
        btn_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //상의
                if (cloth_select == true) {
                    ObjectAnimator TopY = ObjectAnimator.ofFloat(imgTop, "translationY",
                            previous_top_y, after_top_y);

                    TopY.start();
                    previous_top_y += 2;
                    after_top_y += 2;
                }
                //하의
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationY",
                            previous_bottom_y, after_bottom_y);

                    BottomX.start();
                    previous_bottom_y += 2;
                    after_bottom_y += 2;
                }
            }
        });

        //위 화살표 클릭시
        btn_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //상의
                if (cloth_select == true) {
                    ObjectAnimator TopY = ObjectAnimator.ofFloat(imgTop, "translationY",
                            previous_top_y, after_top_y);

                    TopY.start();
                    previous_top_y -= 2;
                    after_top_y -= 2;
                }
                //하의
                else {
                    ObjectAnimator BottomX = ObjectAnimator.ofFloat(imgBottom, "translationY",
                            previous_bottom_y, after_bottom_y);

                    BottomX.start();
                    previous_bottom_y -= 2;
                    after_bottom_y -= 2;
                }
            }
        });

        //상의 하단 메뉴 열림
        btn_TopMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.openDrawer(drawer_bottomView);
                cloth_select = true; //상의 움직임
            }
        });

        //하의 하단 메뉴 열림
        btn_BottomMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_drawer.openDrawer(drawer_bottomView);
                cloth_select = false; //하의 움직임
            }
        });

        drawer_bottomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //년,월,일,시간 포멧 설정
        Date time = new Date(); //파일명 중복 방지를 위해 사용될 현재시간
        String current_time = sdf.format(time); //String형 변수에 저장

        //사진저장 버튼 누르면
        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request_Capture(frameLayout, current_time + "_capture");
            }
        });


        //스위치 켜지면 배경화면 검정색 변경
        swBlackMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                    imgModel.setImageResource(R.drawable.model_black);
                else
                    imgModel.setImageResource(R.drawable.model_white);
            }
        });

        //새로고침버튼 누르면
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebaseStorage 상의 사진 불러오기
                storageReference.child(email).child("Top").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgTop);
                    }
                });

                //firebaseStorage 하의 사진 불러오기
                storageReference.child(email).child("Bottom").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgBottom);
                    }
                });
            }
        });

        //firebaseStorage 상의 사진 불러오기
        storageReference.child(email).child("Top").getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgTop);
                    }
                });

        //firebaseStorage 하의 사진 불러오기
        storageReference.child(email).child("Bottom").getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imgBottom);
                    }
                });

        //메뉴버튼 누르면
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
                //계정이름으로 이미지 불러오기(다른 사람과 겹치지 않기 위한 고유한 이름 필요)
                storageReference.child(email).child("Profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    //firebase storage에 있는 본인계정 아이디로 사진으로 프로필사진 불러옴
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
        //메뉴 닫기 버튼 누르면
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        /*프로필 수정 버튼 누르면*/
        TextView tv_profileEdit = (TextView) findViewById(R.id.tv_profileEdit);

        tv_profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("email", email); //로그인 할 때 사용한 email 전송
                drawerLayout.closeDrawers();
                startActivity(intent);
            }
        });

        /*이용가이드 버튼 누르면*/
        TextView tv_explain = (TextView) findViewById(R.id.tv_explain);

        tv_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AnnounceActivity.class);
                drawerLayout.closeDrawers();
                startActivity(intent);
            }
        });

        /*바텀네비게이션*/
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

        /*로그아웃*/
        mFirebaseAuth = FirebaseAuth.getInstance();

        TextView tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃 하기
                mFirebaseAuth.signOut();
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate()


    public void Request_Capture(View view, String title) {
        if (view == null) { //Null Point Exception ERROR 방지
            System.out.println("시스템 오류");
            return;
        }

        /* 캡쳐 파일 저장 */
        view.buildDrawingCache(); //캐시 비트 맵 만들기
        Bitmap bitmap = view.getDrawingCache();
        FileOutputStream fos;

        /* 저장할 폴더 Setting */
        File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/"); //저장 경로 (File Type형 변수)


        if (!uploadFolder.exists()) { //만약 경로에 폴더가 없다면
            uploadFolder.mkdir(); //폴더 생성
        }

        /* 파일 저장 */
        String Str_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/"; //저장 경로 (String Type 변수)

        try {
            fos = new FileOutputStream(Str_Path + title + ".jpg"); // 경로 + 제목 + .jpg로 FileOutputStream Setting
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            Toast.makeText(MainActivity.this, "사진저장 성공", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//Request_Capture


    //프래그먼트 교체 실행문
    private void setFrag(int n) {
        switch (n) {
            case 0:
                break;
            case 1:
                Intent intent = getIntent(); //로그인 한 email 받기 위한 intent
                String email = intent.getStringExtra("email"); //LoginActivity에서 email 값 받아옴
                Intent intent2 = new Intent(MainActivity.this, Cloth.class);
                intent2.putExtra("email", email);
                startActivity(intent2);
                break;
        }
    }
}//MainActivity`