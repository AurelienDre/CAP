package com.example.cap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cap.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(AddFragment.this)
                        .navigate(R.id.action_AddFragment_to_MapFragment)
        );

        binding.buttonSave.setOnClickListener(v ->
                        NavHostFragment.findNavController(AddFragment.this)
        );
        binding.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nom = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.buttonAddPictures.setOnClickListener(v -> {
            binding.textviewSelectedPictures.setText("test");
        });
        binding.buttonAddMap.setOnClickListener(v -> {
            binding.textviewSelectedPictures.setText("test");
        });
        binding.textviewSelectedPictures.setText("test");
        binding.textViewSelectedMap.setText("test");
        binding.editTextDescription.getText();
        binding.editTextContact.getText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}