package br.com.mateus.brito.appautomacaoestacionamento;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingAlert {
    private Activity activity;
    private AlertDialog dialog;

    LoadingAlert(Activity activity) {
        this.activity = activity;
    }

    void startAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog_layout, null));

        builder.setCancelable(true);

        this.dialog = builder.create();
        this.dialog.show();
    }

    void closeAlertDialog(){
        this.dialog.dismiss();;
    }
}
