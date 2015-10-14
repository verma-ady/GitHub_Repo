package com.swiftintern.www.githubrepo;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link home.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link home#newInstance} factory method to
// * create an instance of this fragment.
// */

public class home extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment home.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static home newInstance(String param1, String param2) {
//        home fragment = new home();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public home() {
        // Required empty public constructor
    }

    database d;
    View root;
    ListView lv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        d=new database(getActivity());
        lv = (ListView) root.findViewById(R.id.followlistView);
        showlist();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), onListClick.class).putExtra(Intent.EXTRA_TEXT, name);
                startActivity(intent);
            }
        });

        return  root;
    }

    @Override
    public void onResume() {
        showlist();
        super.onResume();
    }

    public void showlist(){

        Cursor dbres = d.onShow() ;

        int cnt = dbres.getCount();
        int i=0;
        String res[];
        if( cnt!=0 ) {
            res = new String[cnt];
            while(dbres.moveToNext()) {
                res[i++] = dbres.getString(1);
            }
        }
        else {
            res = new String[0];
            Toast.makeText(getActivity(), "Followed List Empty ", Toast.LENGTH_SHORT).show();
        }
        ArrayList<String> arlist = new ArrayList<>(Arrays.asList(res));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_home, R.id.followlist, arlist);
        lv.setAdapter(arrayAdapter);
    }


    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
