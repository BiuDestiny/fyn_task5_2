package com.example.fyn_task5_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PhoneDialog {
    private Context context;
    private String title;

    public interface OnDialogSubmitListener {
        public void onSubmit(String updateName, String updatePhone);
    }

    public PhoneDialog(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public void showDialog(String name, String phone, final OnDialogSubmitListener l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null, false);
        final EditText et_name = view.findViewById(R.id.dialog_view_et_name);
        final EditText et_phone = view.findViewById(R.id.dialog_view_et_phone);
        et_name.setText(name);
        et_phone.setText(phone);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                l.onSubmit(et_name.getText().toString(),et_phone.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();

    }
}
