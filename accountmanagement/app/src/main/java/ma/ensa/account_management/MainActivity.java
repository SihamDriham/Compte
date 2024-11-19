package ma.ensa.account_management;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.account_management.adapter.CompteAdapter;
import ma.ensa.account_management.model.Compte;
import ma.ensa.account_management.api.ApiInterface;
import ma.ensa.account_management.service.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private boolean isXmlFormat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Spinner spinnerFormat = findViewById(R.id.spinner_format);
        spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    isXmlFormat = true;
                    Log.d("Spinner", "Format sélectionné : XML");
                } else {
                    isXmlFormat = false;
                    Log.d("Spinner", "Format sélectionné : JSON");
                }
                fetchComptes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "Aucun format sélectionné");
            }
        });

        fetchComptes();
    }

    private void fetchComptes() {
        String acceptHeader = isXmlFormat ? "application/xml" : "application/json";

        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);

        api.getAllComptes(acceptHeader).enqueue(new Callback<List<Compte>>() {
            @Override
            public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                if (response.body() != null) {
                    List<Compte> comptes = response.body();
                    recyclerView.setAdapter(new CompteAdapter(comptes, MainActivity.this));
                } else {
                    Log.e("fetchComptes", "Aucune donnée reçue");
                }
            }

            @Override
            public void onFailure(Call<List<Compte>> call, Throwable t) {
                Log.e("fetchComptes", "Erreur : " + t.getMessage());
            }
        });
    }

    public void updateCompte(Long id, Compte currentCompte) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Modifier le compte");

        View dialogView = getLayoutInflater().inflate(R.layout.activity_update, null);
        EditText editTextSolde = dialogView.findViewById(R.id.editTextSolde);
        EditText editTextDateCreation = dialogView.findViewById(R.id.editTextDateCreation);
        EditText editTextType = dialogView.findViewById(R.id.editTextType);

        editTextSolde.setText(String.valueOf(currentCompte.getSolde()));
        editTextDateCreation.setText(currentCompte.getDateCreation());
        editTextType.setText(currentCompte.getType());

        builder.setView(dialogView)
                .setPositiveButton("Modifier", (dialog, which) -> {
                    double newSolde = Double.parseDouble(editTextSolde.getText().toString());
                    String newDateCreation = editTextDateCreation.getText().toString();
                    String newType = editTextType.getText().toString();

                    Compte updatedCompte = new Compte(id, newSolde, newDateCreation, newType);

                    updateCompteInDatabase(id, updatedCompte);
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateCompteInDatabase(Long id, Compte updatedCompte) {
        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);

        api.updateCompte(id, updatedCompte).enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Compte modifier avec succès", Toast.LENGTH_SHORT).show();
                    fetchComptes();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la modification à jour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                Log.e(TAG, "Erreur lors de la modification du compte : " + t.getMessage());
                Toast.makeText(MainActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCompte(Long id) {
        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);
        api.deleteCompte(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show();
                    fetchComptes();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Erreur lors de la suppression du compte : " + t.getMessage());
                Toast.makeText(MainActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
            }
        });
    }
}