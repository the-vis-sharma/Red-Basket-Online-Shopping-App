package com.skylark.redbasket;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Red Basket");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        Menu menu = navigationView.getMenu();
        if(pref.getString("isLogin", "false").equals("false")) {
            menu.findItem(R.id.nav_logout).setVisible(false);
        }

        FragmentManager manager = getSupportFragmentManager();
        ShopNowFragment snf = new ShopNowFragment();
        manager.beginTransaction().replace(R.id.RelativeLayoutHome, snf, snf.getTag()).commit();
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(count > 0) {
            manager.popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String isLogin = pref.getString("isLogin", "false");

        FragmentManager manager = getSupportFragmentManager();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_cart) {
            if(isLogin.equals("true")) {
                ViewCartFragment vcf = new ViewCartFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, vcf, vcf.getTag()).addToBackStack(null).commit();
            }
            else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Login Required")
                        .setMessage("Login First to get personalized experince of shopping.")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create()
                        .show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        FragmentManager manager = getSupportFragmentManager();

        String isLogin = pref.getString("isLogin", "false");

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            ShopNowFragment snf = new ShopNowFragment();
            manager.beginTransaction().replace(R.id.RelativeLayoutHome, snf, snf.getTag()).commit();
        } else if (id == R.id.nav_cart) {
            if(isLogin.equals("true")) {
                ViewCartFragment vcf = new ViewCartFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, vcf, vcf.getTag()).addToBackStack(null).commit();
            }
            else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Login Required")
                        .setMessage("Login First to get personalized experince of shopping.")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_my_orders) {
            if(isLogin.equals("true")) {
                MyOrderFragment mof = new MyOrderFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, mof, mof.getTag()).addToBackStack(null).commit();
            }
            else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Login Required")
                        .setMessage("Login First to get personalized experince of shopping.")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_my_account) {
            if(isLogin.equals("true")) {
                MyAccountFragment myAccountFragment = new MyAccountFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, myAccountFragment, myAccountFragment.getTag()).addToBackStack(null).commit();
            }
            else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Login Required")
                        .setMessage("Login First to get personalized experince of shopping.")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_help_center) {

        } else if (id == R.id.nav_privacy_policy) {

        } else if (id == R.id.nav_faqs) {

        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user_id", "");
            editor.putString("isLogin", "false");
            editor.putString("lastLoginTime", "");
            editor.commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
