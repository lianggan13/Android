package com.example.zhangliang.videoprojects.ui.skill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.zhangliang.videoprojects.databinding.FragmentSkillBinding;

import io.noties.markwon.Markwon;

public class SkillFragment extends Fragment {

    private FragmentSkillBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SkillViewModel viewModel =
                new ViewModelProvider(this).get(SkillViewModel.class);

        binding = FragmentSkillBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;

        // observe markdown from ViewModel and render with Markwon
        viewModel.getMarkdown().observe(getViewLifecycleOwner(), md -> {
            String content = md == null ? "" : md;
            try {
                Markwon markwon = Markwon.create(requireContext());
                markwon.setMarkdown(textView, content);
            } catch (Exception e) {
                android.util.Log.e("SkillFragment", "render markdown failed", e);
                textView.setText(content);
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