package mg.itu.lazanomentsoa.itutptparis.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mg.itu.lazanomentsoa.itutptparis.databinding.FragmentQrcodeBinding;
import mg.itu.lazanomentsoa.itutptparis.views.AbstractBaseFragment;


public class QrcodeFragment extends AbstractBaseFragment {
    private FragmentQrcodeBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQrcodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }
}