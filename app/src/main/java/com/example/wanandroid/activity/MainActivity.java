package com.example.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.R;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.fragment.ChapterFragment;
import com.example.wanandroid.fragment.HomeFragment;
import com.example.wanandroid.utils.ImgTransUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Article> articleList;
    private DrawerLayout mDrawerLayout;
    private boolean isHide = true;   //设置为隐藏
    private RecyclerView recyclerView;
    private TextView tvName;
    private ImageView imgAvatar;
    private int page = 0;
    public  static SharedPreferences mPref;
    private Uri imageUri;
    private ViewPager mViewPager;
    private NavigationView navigationView;
    private List<Fragment> fragmentList;
    private HomeFragment homeFragment;
    private ChapterFragment chapterFragment;
    private ViewPager contentViewPager;
    private BottomNavigationView bottomNav;
    private TextView tvTitle;
    private RecyclerView mRecyclerView;
    public MainActivity() {

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化成员变量
        if(articleList == null)
            articleList = new ArrayList<>();
        if(fragmentList == null)
            fragmentList = new ArrayList<>();
        tvTitle = findViewById(R.id.bar_title);
        homeFragment = new HomeFragment();
        chapterFragment = new ChapterFragment();
        contentViewPager = findViewById(R.id.content_view_Pager);
        mRecyclerView = contentViewPager.findViewById(R.id.recycler_view);
        bottomNav = findViewById(R.id.bottom_nav);
        mPref = getSharedPreferences("data", MODE_PRIVATE);
        navigationView =  findViewById(R.id.navigation_view);
        View headLayout = navigationView.inflateHeaderView(R.layout.include_header);
        tvName =  headLayout.findViewById(R.id.tv_name);
        imgAvatar = headLayout.findViewById(R.id.img_avatar);
        mViewPager =  findViewById(R.id.viewPager);
        recyclerView =  findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);
        fragmentList.add(homeFragment);
        fragmentList.add(chapterFragment);

        //设置缓存页面的数量
        contentViewPager.setOffscreenPageLimit(fragmentList.size());
        contentViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        //底部导航栏的子菜单项的点击事件
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        contentViewPager.setCurrentItem(0);
                        tvTitle.setText("玩Android Demo");
                        break;
                    case R.id.nav_chapter:
                        contentViewPager.setCurrentItem(1);
                        tvTitle.setText("公众号");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //viewpager切换页面监听事件
        contentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //设置 进入首页滑动菜单的按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        //子页面 菜单栏点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_about:
                        Intent i = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.menu_exit: {
                        if (tvName.getText().equals("未登录")) {
                            Toast.makeText(MainActivity.this, "您还未登录！", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            tvName.setText("未登录");
                            imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                            startActivity(intent);
                        }
                    }
                    break;
                    default:
                        break;
                }
                return true;
            }
        });

        //登录成功之后设置侧滑页用户名
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        if (!TextUtils.isEmpty(userName)) {
            tvName.setText(userName);
        }
        String imgInfo = mPref.getString(tvName.getText().toString(), "");
        if (!imgInfo.equals("")) {
            Bitmap bitmap = ImgTransUtils.StrToBit(imgInfo);
            imgAvatar.setImageBitmap(bitmap);
        }

        //侧滑页用户名点击事件
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        //侧滑页头像点击事件
        try {
            //imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvName.getText().toString().equals("未登录")) {
                        //此时用户未登录  点击头像跳转登陆界面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        //用户已登录  点击头像更换头像  可选择在相册添加  也可选择使用手机拍照
                        changeImage();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends FragmentPagerAdapter{
        public MyAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    //改变头像的图片 1.拍照 2.从图库选择
    private void changeImage() {
        final String font[] = {"拍照", "从图库选择"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(font, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (font[which]) {
                    case "拍照":
                        //创建File对象，用于存储拍照后的图片
                        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.wanandroid.fileprovider", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机程序
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, 1);
                        break;
                    case "从图库选择":
                        //检查是否有读取存储文件的权限
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            openAlbum();
                        }

                }
            }
        }).setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null).create().show();
    }
    //打开相册
    private void openAlbum() {
        Intent i = new Intent("android.intent.action.GET_CONTENT");
        i.setType("image/*");
        startActivityForResult(i, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "您拒绝了访问存储文件权限", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imgAvatar.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    String key = tvName.getText().toString();
                                    editor.putString(key, ImgTransUtils.BitToStr(bitmap));
                                    editor.apply();
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "缓存用户图片成功", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2: {
                if (resultCode == RESULT_OK) {
                    //判断系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //把从相册选择的图片显示出来
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imgAvatar.setImageBitmap(bitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        String key = tvName.getText().toString();
                        editor.putString(key, ImgTransUtils.BitToStr(bitmap));
                        editor.apply();
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "缓存用户图片成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    //标题栏子控件 搜索控件、返回控件的点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search: {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
            break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

}