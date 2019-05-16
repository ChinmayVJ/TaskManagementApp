package com.example.user.officemanagementapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.officemanagementapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fabButton;

    //Firebase

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    //Recycler View ..
    // it is almost like list view, just more efficient, it allocates memory only to the data
    // which is visible on screen to user at the moment

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        toolbar =(Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( "Your Task App" );

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String mUid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Task Note" ).child( mUid );
        mDatabase.keepSynced( true );

        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );

        LinearLayoutManager layoutManager = new LinearLayoutManager( this );

        layoutManager.setReverseLayout( true );
        layoutManager.setStackFromEnd( true );

        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( layoutManager );

        fabButton = (FloatingActionButton) findViewById( R.id.fab_btn );
        fabButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myDialog = new AlertDialog.Builder( HomeActivity.this );

                LayoutInflater inflater = LayoutInflater.from( HomeActivity.this );

                View myView = inflater.inflate( R.layout.custominputfield, null );

                myDialog.setView( myView );
                final AlertDialog dialog = myDialog.create();

                final EditText title = (EditText) myView.findViewById( R.id.edit_title );
                final EditText note = (EditText) myView.findViewById( R.id.edit_note );

                Button saveButton = (Button) myView.findViewById( R.id.save_btn );

                saveButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String mtitle = title.getText().toString().trim();
                        String mnote = note.getText().toString().trim();

                        if (TextUtils.isEmpty( mtitle ))
                        {
                            title.setError( "* Title Required *" );
                            return;
                        }
                        if (TextUtils.isEmpty( mnote ))
                        {
                            note.setError( "* Note Required *" );
                            return;
                        }

                        String id = mDatabase.push().getKey();
                        String Date = DateFormat.getDateInstance().format( new Date(  ) );
                        Data data = new Data( mtitle, mnote, Date, id );
                        mDatabase.child( id ).setValue( data );
                        Toast.makeText( HomeActivity.this, "Data Inserted", Toast.LENGTH_SHORT ).show();
                        dialog.dismiss();
                    }
                } );

                dialog.show();

            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data Model, final int position) {

                final Data model = Model;

                viewHolder.setTitle( model.getTitle() );
                viewHolder.setNote( model.getNote() );
                viewHolder.setDate( model.getDate() );

                viewHolder.myView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String post_key = getRef( position ).getKey();

                        updateData(model.getTitle(), model.getNote(), post_key);
                    }
                } );

            }
        };

        recyclerView.setAdapter( adapter );

    }

    public void updateData(final String Title, final String Note, final String PostKey){

        AlertDialog.Builder myDialog = new AlertDialog.Builder( HomeActivity.this );
        LayoutInflater inflater = LayoutInflater.from( HomeActivity.this );

        View mView = inflater.inflate( R.layout.updateinputfield,null );
        myDialog.setView( mView );

        final AlertDialog dialog=myDialog.create();
        dialog.show();

        final EditText upTitle = mView.findViewById( R.id.update_title );
        upTitle.setText( Title );

        final EditText upNote =  mView.findViewById( R.id.update_note );
        upNote.setText( Note );

        Button updateButton = mView.findViewById( R.id.update_btn );
        Button deleteButton = mView.findViewById( R.id.delete_btn );

        updateButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newTitle = upTitle.getText().toString().trim();
                String newNote = upNote.getText().toString().trim();

                String Date = DateFormat.getDateInstance().format( new Date(  ) );
                Data data = new Data( newTitle, newNote, Date, PostKey );
                mDatabase.child( PostKey ).setValue( data );

                dialog.dismiss();
            }
        } );

        deleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child( PostKey ).removeValue();
                dialog.dismiss();
            }
        } );

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View myView;

        public MyViewHolder(View itemView) {
            super( itemView );
            myView = itemView;
        }

        public void setTitle(String title)
        {
            TextView mTitle = (TextView) myView.findViewById( R.id.data_title );
            mTitle.setText( title );
        }

        public void setNote(String note)
        {
            TextView mNote = (TextView) myView.findViewById( R.id.data_note );
            mNote.setText( note );
        }

        public void setDate(String date)
        {
            TextView mDate = (TextView) myView.findViewById( R.id.data_date );
            mDate.setText( date );
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.mainmenu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                mAuth.signOut();
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                break;
        }
        return super.onOptionsItemSelected( item );
    }
}
