package com.example.notesnest.dashboard.ui.slideshow;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.notesnest.R;
import com.example.notesnest.dashboard.DashboardActivity;
import com.example.notesnest.databinding.FragmentSlideshowBinding;
import com.example.notesnest.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(root.getContext(), LoginActivity.class);
                    intent.putExtra("logout","");
                    startActivity(intent);
                    getActivity().finish();

                })
                .setNegativeButton("No",((dialog, which) -> {
                    startActivity(new Intent(root.getContext(), DashboardActivity.class));
                    getActivity().finish();
                }))
                .setIcon(R.drawable.alert_icon)
                .show();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}