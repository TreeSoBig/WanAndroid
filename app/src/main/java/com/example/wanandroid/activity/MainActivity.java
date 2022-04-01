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
import com.example.wanandroid.fragment.ChapterFragment;
import com.example.wanandroid.fragment.HomeFragment;
import com.example.wanandroid.utils.ImgTransUtils;
import com.example.wanandroid.utils.ThreadPoolManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView mTvName;
    private ImageView mImgAvatar;
    private Uri mImageUri;
    private List<Fragment> mFragmentList;
    private ViewPager mContentViewPager;
    private BottomNavigationView mBottomNav;
    private TextView mTvTitle;
    private  ThreadPoolManager mThreadPool;
    private final String PROVIDER_AUTHORITY="com.example.wanandroid.fileprovider";
    private final String IMAGE_CAPTURE_ACTION="android.media.action.IMAGE_CAPTURE";
    public static String USER_NAME_KEY="userName";
    private final String FILE_NAME="data";
    private Runnable task;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentList = new ArrayList<>();
        mTvTitle = findViewById(R.id.bar_title);
        mContentViewPager = findViewById(R.id.content_view_Pager);
        mBottomNav = findViewById(R.id.bottom_nav);
        NavigationView homeNavigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        View headLayout = homeNavigationView.inflateHeaderView(R.layout.include_header);
        mTvName = headLayout.findViewById(R.id.tv_name);
        mImgAvatar = headLayout.findViewById(R.id.img_avatar);
        HomeFragment homeFragment = new HomeFragment();
        ChapterFragment chapterFragment = new ChapterFragment();
        SharedPreferences userPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        setSupportActionBar(toolbar);
        mFragmentList.add(homeFragment);
        mFragmentList.add(chapterFragment);
        mThreadPool = ThreadPoolManager.getInstance();

        //设置缓存页面的数量
        mContentViewPager.setOffscreenPageLimit(mFragmentList.size());
        mContentViewPager.setAdapter(new HomeViewPagerAdapter(getSupportFragmentManager()));

        //底部导航栏的子菜单项的点击事件
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mContentViewPager.setCurrentItem(0);
                        mTvTitle.setText(R.string.app_name);
                        break;
                    case R.id.nav_chapter:
                        mContentViewPager.setCurrentItem(1);
                        mTvTitle.setText(R.string.chapter);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //viewpager切换页面监听事件
        mContentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomNav.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //设置 进入首页滑动菜单的按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        //子页面 菜单栏点击事件
        homeNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_about:
                        Intent i = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.menu_exit: {
                        if (mTvName.getText().equals(getString(R.string.Initially_userName))) {
                            Toast.makeText(MainActivity.this, getString(R.string.not_login), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            mTvName.setText(getString(R.string.Initially_userName));
                            mImgAvatar.setImageResource(R.mipmap.ic_launcher_round);
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

        //设置侧滑页用户名
        Intent intent = getIntent();
        String userName = intent.getStringExtra(USER_NAME_KEY);
        if (!TextUtils.isEmpty(userName)) {
            mTvName.setText(userName);
            String imgInfo = userPref.getString(userName, "");
            if (!imgInfo.equals("")) {
                Bitmap bitmap = ImgTransUtils.StrToBit(imgInfo);
                mImgAvatar.setImageBitmap(bitmap);
            }
        }

        //侧滑页用户名点击事件
        mTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        //侧滑页头像点击事件
        try {
            //imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
            mImgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTvName.getText().toString().equals(getString(R.string.Initially_userName))) {
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

    class HomeViewPagerAdapter extends FragmentPagerAdapter {
        public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    //改变头像的图片 1.拍照 2.从图库选择
    private void changeImage() {
        final String takePhoto = "拍照";
        final String fromAlbum = "从图库选择";
        final String font[] = {takePhoto, fromAlbum};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(font, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (font[which]) {
                    case takePhoto:
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
                            mImageUri = FileProvider.getUriForFile(MainActivity.this, PROVIDER_AUTHORITY, outputImage);
                        } else {
                            mImageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机程序
                        Intent intent = new Intent(IMAGE_CAPTURE_ACTION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(intent, 1);
                        break;
                    case fromAlbum:
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
        String GET_CONTENT_ACTION = "android.intent.action.GET_CONTENT";
        Intent i = new Intent(GET_CONTENT_ACTION);
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
                    Toast.makeText(this, getString(R.string.refuse_grant), Toast.LENGTH_SHORT).show();
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
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                        mImgAvatar.setImageBitmap(bitmap);
                        task = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
                                    String key = mTvName.getText().toString();
                                    editor.putString(key, ImgTransUtils.BitToStr(bitmap));
                                    editor.apply();
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, getString(R.string.storage_picture_success), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        mThreadPool.execute(task);
                    } catch (IOException e) {
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
            String MEDIA_DOCUMENT_AUTHORITY = "com.android.providers.media.documents";
            String DOWNLOAD_DOCUMENT_AUTHORITY = "com.android.providers.downloads.documents";
            if (MEDIA_DOCUMENT_AUTHORITY.equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if (DOWNLOAD_DOCUMENT_AUTHORITY.equals(uri.getAuthority())) {
                String CONTENT_URI = "content://downloads/public_downloads";
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(CONTENT_URI), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
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
            mImgAvatar.setImageBitmap(bitmap);
            task = new Runnable() {
                @Override
                public void run() {
                    try {
                        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
                        String key = mTvName.getText().toString();
                        editor.putString(key, ImgTransUtils.BitToStr(bitmap));
                        editor.apply();
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, getString(R.string.storage_picture_success), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThreadPool.execute(task);
        } else {
            Toast.makeText(this, getString(R.string.get_picture_failure), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadPool.shutdown();
    }
}