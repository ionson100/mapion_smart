package com.example.mapion.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mapion.R;
import com.example.mapion.databinding.FragmentSlideshowBinding;
import com.example.mapion.models.TotalSettings;
import com.example.mapion.settings.IActionSettings;
import com.example.mapion.settings.SettingsBuilder;

public class SettingsFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    TotalSettings totalSettings= TotalSettings.getInstance();
    SettingsBuilder settingsBuilder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView list_settings = root.findViewById(R.id.list_settings);

        settingsBuilder=new SettingsBuilder(getContext(), totalSettings,list_settings, new IActionSettings() {
            @Override
            public String action(Object o) {
                totalSettings.save();
                settingsBuilder.refresh();
                return null;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}