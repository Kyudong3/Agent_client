package com.kyudong.agent_client.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.kyudong.agent_client.Dialog.CustomDialog;
import com.kyudong.agent_client.R;
import com.kyudong.agent_client.WriteBoard.BoardWrite;

public class BoardMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    private BoardFree boardFreeFragment;
    private BoardBusan boardBusan;
    private BoardDaejeon boardDaejeon;
    private BoardDaegu boardDaegu;
    private BoardGwangJu boardGwangJu;
    private BoardIncheon boardIncheon;
    private BoardJeju boardJeju;
    private BoardSeoul boardSeoul;
    private BoardUlleungDo boardUlleungDo;
    private BoardUlsan boardUlsan;

    private String seqNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        toolbar = (Toolbar) findViewById(R.id.board_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        seqNo = intent.getStringExtra("user_seqNo");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent writeBoardIntent = new Intent(getApplicationContext(), BoardWrite.class);
                writeBoardIntent.putExtra("user_seqNo", seqNo);
                startActivity(writeBoardIntent);
            }
        });

        findFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.board_fragment, boardFreeFragment).commit();
    }

    private void findFragment() {
        boardFreeFragment = new BoardFree().newInstance();
        boardBusan = new BoardBusan().newInstance();
        boardDaejeon = new BoardDaejeon().newInstance();
        boardDaegu = new BoardDaegu().newInstance();
        boardGwangJu = new BoardGwangJu().newInstance();
        boardIncheon = new BoardIncheon().newInstance();
        boardJeju = new BoardJeju().newInstance();
        boardSeoul = new BoardSeoul().newInstance();
        boardUlleungDo = new BoardUlleungDo().newInstance();
        boardUlsan = new BoardUlsan().newInstance();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refreshMenu) {
            Toast.makeText(getApplicationContext(), "서비스 준비중입니다", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.logoutMenu) {
            CustomDialog customDialog = new CustomDialog(this);
            customDialog.show();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.free:
                transactionMenuFragment(boardFreeFragment);
                break;

            case R.id.Seoul:
                transactionMenuFragment(boardSeoul);
                break;

            case R.id.GwangJu:
                transactionMenuFragment(boardGwangJu);
                break;

            case R.id.Daegu:
                transactionMenuFragment(boardDaegu);
                break;

            case R.id.Daejeon:
                transactionMenuFragment(boardDaejeon);
                break;

            case R.id.Busan:
                transactionMenuFragment(boardBusan);
                break;

            case R.id.Incheon:
                transactionMenuFragment(boardIncheon);
                break;

            case R.id.Jejudo:
                transactionMenuFragment(boardJeju);
                break;

            case R.id.Ulleungdo:
                transactionMenuFragment(boardUlleungDo);
                break;

            case R.id.Ulsan:
                transactionMenuFragment(boardUlsan);
                break;
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void transactionMenuFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.board_fragment, fragment).commit();
    }
}
