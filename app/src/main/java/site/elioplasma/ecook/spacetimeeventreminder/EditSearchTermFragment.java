package site.elioplasma.ecook.spacetimeeventreminder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by eli on 4/7/16.
 */
public class EditSearchTermFragment extends DialogFragment {

    public static final String EXTRA_SEARCH_TERM =
            "site.elioplasma.ecook.spacetimeeventreminder.searchterm";

    private static final String ARG_SEARCH_TERM = "search_term";

    private EditText mEditText;

    public static EditSearchTermFragment newInstance(String searchTerm) {
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TERM, searchTerm);

        EditSearchTermFragment fragment = new EditSearchTermFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String searchTerm = getArguments().getString(ARG_SEARCH_TERM);

        mEditText = new EditText(getActivity());
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        mEditText.setText(searchTerm);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.search_term_for_sky_map)
                .setView(mEditText)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK, mEditText.getText().toString());
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, String searchTerm) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SEARCH_TERM, searchTerm);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
