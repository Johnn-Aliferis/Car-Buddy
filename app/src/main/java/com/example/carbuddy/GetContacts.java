package com.example.carbuddy;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetContacts extends AsyncTask<Void,Void,HashMap<String,String>>{
    private HashMap<String,String> phoneNumbers = new HashMap<>();
     private Context context;
     public AsyncResponse delegate= new AsyncResponse() {
         @Override
         public void processFinish(HashMap<String, String> output) {
         }
     };

     public GetContacts(Context context){
         this.context=context;
     }

    @Override
    protected void onPostExecute(HashMap<String,String> resultMap) {
        delegate.processFinish(resultMap);
    }

    @Override
    protected HashMap<String,String> doInBackground(Void... voids) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //System.out.println("phone" + phone);
                        phoneNumbers.put(name,phone);
                    }
                    pCur.close();
                }
            }
        }
        return phoneNumbers ;
    }
}