package digital.dispatch.TaxiLimoNewUI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public boolean canOpenTutorial(String className) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPreferencesManager.SP_TUTORIALS, Context.MODE_APPEND);
        return SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, className, true);
    }

    public void stopShowingToolTip(String className) {
        SharedPreferences preference = getActivity().getSharedPreferences(SharedPreferencesManager.SP_TUTORIALS, Context.MODE_APPEND);
        SharedPreferencesManager.savePreferences(preference, className, false);
    }

    public void showToolTip(String tip, final String className) {
        final View view = getView(); //returns base view of the fragment
        if (view == null)
            return;
        if (!(view instanceof ViewGroup)) {
            return;
        }
        final ViewGroup viewGroup = (ViewGroup) view;

        //only inflate tool tip if it is not already showing
        if (view.findViewById(R.id.tooltip) == null) {
            View tooltip = View.inflate(getActivity(), R.layout.tooltip, viewGroup);
            TextView icon_light_bulb = (TextView) tooltip.findViewById(R.id.icon_light_bulb);
            TextView tv_tooltip = (TextView) tooltip.findViewById(R.id.tv_tooltip);
            TextView icon_close = (TextView) tooltip.findViewById(R.id.icon_close);
            tv_tooltip.setText(tip);
            Typeface icon_pack = FontCache.getFont(getActivity(), "fonts/icon_pack.ttf");
            Typeface openSansRegular = FontCache.getFont(getActivity(), "fonts/OpenSansRegular.ttf");
            icon_light_bulb.setTypeface(icon_pack);
            icon_light_bulb.setText(MBDefinition.ICON_LIGHT_BULB);
            icon_close.setTypeface(icon_pack);
            icon_close.setText(MBDefinition.ICON_CROSS_SMALL);
            tv_tooltip.setTypeface(openSansRegular);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewGroup.findViewById(R.id.tooltip).setVisibility(View.GONE);
                    stopShowingToolTip(className);
                }
            };

            viewGroup.findViewById(R.id.tooltip).setOnClickListener(listener);
        }
        else{
            view.findViewById(R.id.tooltip).setVisibility(View.VISIBLE);
        }

    }

    public void hideToolTip() {
        if (getView().findViewById(R.id.tooltip) != null)
            getView().findViewById(R.id.tooltip).setVisibility(View.GONE);
    }


}
