package com.example.fyn_task5_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity {
    PhoneDatabase phoneDatabase;
    int count = 1;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_main);
        phoneDatabase = new PhoneDatabase(this);
        phoneDatabase.open();

        ListView listView = findViewById(R.id.listview);
        final Cursor c = phoneDatabase.quertAll();
        count = c.getCount();
        adapter = new SimpleCursorAdapter(this, R.layout.row_view, c, new String[]{PhoneDatabase.KEY_NAME, PhoneDatabase.KEY_PHONE}, new int[]{R.id.row_view_tv_name, R.id.row_view_tv_phone});
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhoneDatabase.PhoneCursor pc = phoneDatabase.queryById(id);
                pc.moveToFirst();
                String phone = pc.getPhone();
                pc.close();
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opt_add:
                addData();
                break;

            case R.id.opt_reset:
                resetData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        MenuItem search = menu.findItem(R.id.opt_searchView);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor c = phoneDatabase.fuzzyQuery(newText);
                adapter.swapCursor(c);
                updateListView();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.ctx_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = adapterContextMenuInfo.id;
        switch (item.getItemId()) {
            case R.id.ctx_add:
                addData();
                break;
            case R.id.ctx_edit:
                editData(id);
                break;
            case R.id.ctx_delete:
                deleteData(id);
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void updateListView() {
        adapter.getCursor().requery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        phoneDatabase.close();
    }

    private void addData() {
        new PhoneDialog(this, "Add data").showDialog("", "", new PhoneDialog.OnDialogSubmitListener() {
            @Override
            public void onSubmit(String updateName, String updatePhone) {
                phoneDatabase.insertData(updateName, updatePhone);
                updateListView();
            }
        });
    }

    private void editData(final long id) {
        PhoneDatabase.PhoneCursor c = phoneDatabase.queryById(id);
        c.moveToFirst();
        String name = c.getName();
        String phone = c.getPhone();
        c.close();
        new PhoneDialog(this, "Edit data").showDialog(name, phone, new PhoneDialog.OnDialogSubmitListener() {
            @Override
            public void onSubmit(String updateName, String updatePhone) {
                phoneDatabase.updateData(updateName, updatePhone, id);
                updateListView();
            }
        });

    }

    private void deleteData(long id) {
        phoneDatabase.delete(id);
        updateListView();
    }

    private void resetData() {
        phoneDatabase.reset();
        count = 1;
        updateListView();
    }
}
