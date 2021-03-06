
package net.debian.debiandroid.contentfragments;

import java.util.ArrayList;
import java.util.Set;

import net.debian.debiandroid.AutoGroupCollapseListener;
import net.debian.debiandroid.DExpandableAdapter;
import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.R;
import net.debian.debiandroid.SettingsActivity;
import net.debian.debiandroid.apiLayer.BTS;
import net.debian.debiandroid.apiLayer.PTS;
import net.debian.debiandroid.utils.SearchCacher;
import net.debian.debiandroid.utils.UIUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class SUBSFragment extends ItemFragment {

    private ArrayList<String> parentItems;
    private ArrayList<Object> childItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.subs_fragment, container, false);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.subscriptions);

        setupSubsList((ExpandableListView) rootView.findViewById(R.id.subscriptionlist));

        return rootView;
    }

    public void setupSubsList(ExpandableListView expandableList) {
        expandableList.setDividerHeight(1);
        expandableList.setClickable(true);
        setSubscribedData();

        final DExpandableAdapter adapter = new DExpandableAdapter(parentItems, childItems);
        adapter.setInflater((LayoutInflater) getSherlockActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE));
        expandableList.setAdapter(adapter);
        registerForContextMenu(expandableList);

        // Add autocollapsing of list if enabled
        if (SettingsActivity.isAutoCollapseEnabled(getSherlockActivity())) {
            expandableList.setOnGroupExpandListener(new AutoGroupCollapseListener(expandableList));
        }

        expandableList.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
                    int childPosition, long id) {
                String itemClicked = ((TextView) view).getText().toString();
                if (groupPosition == 0) {
                    SearchCacher.setLastSearchByPckgName(itemClicked);
                    // Move to pts fragment
                    UIUtils.loadFragment(getActivity().getSupportFragmentManager(), ContentHelper.PTS, null,
                            true);
                } else if (groupPosition == 1) {
                    String[] items = itemClicked.split("\\|");
                    if (items.length > 1) {
                        SearchCacher.setLastBugSearch(items[0], items[1]);
                        // Move to bts fragment
                        UIUtils.loadFragment(getActivity().getSupportFragmentManager(), ContentHelper.BTS,
                                null, true);
                    }
                }
                return true;
            }
        });
    }

    public void setSubscribedData() {
        parentItems = new ArrayList<String>();
        childItems = new ArrayList<Object>();

        Context context = getSherlockActivity().getApplicationContext();
        Set<String> ptsSubs = new PTS(context).getSubscriptions();
        Set<String> btsSubs = new BTS(context).getSubscriptions();

        parentItems.add(getString(R.string.subscribed_packages, ptsSubs.size()));
        parentItems.add(getString(R.string.subscribed_bugs, btsSubs.size()));

        childItems.add(new ArrayList<String>(ptsSubs));
        childItems.add(new ArrayList<String>(btsSubs));
    }
}
