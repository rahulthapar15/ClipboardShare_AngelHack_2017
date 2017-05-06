package com.sharedclipboard.ui.fragment;

//import android.app.LoaderManager;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sharedclipboard.R;
import com.sharedclipboard.storage.db.models.Clipping;
import com.sharedclipboard.ui.asyncTasks.TextClippingsDataLoader;

import java.util.ArrayList;
import java.util.Arrays;

//import android.content.Loader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TextClippingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TextClippingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextClippingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Clipping>> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextClippingArrayAdapter adapter;
    Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TextClippingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TextClippingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TextClippingsFragment newInstance(String param1, String param2) {
        TextClippingsFragment fragment = new TextClippingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View textClippingFragmentView = inflater.inflate(R.layout.fragment_text_clippings, container, false);

        String s[] = {"the", "world", ""};
        ArrayList<String> sList = new ArrayList<String>(Arrays.asList(s));
        adapter = new TextClippingArrayAdapter(getActivity(), sList);
        ListView list = (ListView) textClippingFragmentView.findViewById(R.id.list);
        if(adapter != null)
            list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        context = getActivity();

        return textClippingFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onResume() {
        super.onResume();

        //async load of the entries
        getLoaderManager().initLoader(0, null, this);
    }

    public void load() {
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<ArrayList<Clipping>> onCreateLoader(int i, Bundle bundle) {
        return new TextClippingsDataLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Clipping>> loader, ArrayList<Clipping> clippings) {
        //give the adapter the clippings
        adapter.clear();
        for(Clipping clipping : clippings) {
            adapter.add(clipping.getClipping());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Clipping>> loader) {
        adapter.clear();
    }
}
