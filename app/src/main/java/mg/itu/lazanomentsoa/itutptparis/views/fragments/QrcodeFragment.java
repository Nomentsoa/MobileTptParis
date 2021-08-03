package mg.itu.lazanomentsoa.itutptparis.views.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import mg.itu.lazanomentsoa.itutptparis.R;
import mg.itu.lazanomentsoa.itutptparis.backendnodejs.models.Match;
import mg.itu.lazanomentsoa.itutptparis.backendnodejs.models.Pari;
import mg.itu.lazanomentsoa.itutptparis.databinding.FragmentQrcodeBinding;
import mg.itu.lazanomentsoa.itutptparis.utils.SessionManager;
import mg.itu.lazanomentsoa.itutptparis.utils.StringConstant;
import mg.itu.lazanomentsoa.itutptparis.viewmodel.QRCodeViewModel;
import mg.itu.lazanomentsoa.itutptparis.views.AbstractBaseFragment;


public class QrcodeFragment extends AbstractBaseFragment {

    private final String TAG = QrcodeFragment.class.getName();
    private FragmentQrcodeBinding binding;
    private QRCodeViewModel qrCodeViewModel;
    private SurfaceView surfaceView;
    private TextView txtBarcodeValue, tvNoMatch, tvDateHeure, tvNomEquipe1, tvNomEquipe2,tvLabelEquipe1, tvLabelEquipe2;
    private Button btnCoteEquipe1, btnCoteEquipe2, btnCoteNull;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    private LifecycleOwner lifecycleOwner;
    private CardView cvCam;
    private ConstraintLayout clMath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQrcodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lifecycleOwner = this;

        qrCodeViewModel = new ViewModelProvider(this).get(QRCodeViewModel.class);



        initViews(root);
        initialiseDetectorsAndSources();

        txtBarcodeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cvCam.getVisibility() == View.GONE){
                    showLoading(false);
                    qrCodeViewModel.getMatchById(s.toString()).observe(lifecycleOwner, retourMatch -> {

                        if (retourMatch == null) {
                            tvNoMatch.setText(getResources().getText(R.string.no_match));
                        }else{
                            if(retourMatch.getMatch() == null){
                                tvNoMatch.setText(getResources().getText(R.string.no_match));
                            }else{
                                tvNoMatch.setVisibility(View.GONE);
                                clMath.setVisibility(View.VISIBLE);

                                String dateMatch = StringConstant.dateFormat.format(retourMatch.getMatch().getDate());
                                tvDateHeure.setText(dateMatch + " à " + retourMatch.getMatch().getHeure());
                                tvNomEquipe1.setText(retourMatch.getMatch().getEquipe1().getNom());
                                tvNomEquipe2.setText(retourMatch.getMatch().getEquipe2().getNom());
                                tvLabelEquipe1.setText(retourMatch.getMatch().getEquipe1().getNom());
                                tvLabelEquipe2.setText(retourMatch.getMatch().getEquipe2().getNom());
                                btnCoteEquipe1.setText(retourMatch.getMatch().getCoteEquipe1()+"");
                                btnCoteEquipe2.setText(retourMatch.getMatch().getCoteEquip2()+"");
                                btnCoteNull.setText(retourMatch.getMatch().getCoteMatchNull()+"");

                               listenerButtonMatch(retourMatch.getMatch());
                            }
                        }
                        dismissLoading();
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;


    }


    private void initViews(View view) {
        txtBarcodeValue = view.findViewById(R.id.txtBarcodeValue);
        surfaceView = view.findViewById(R.id.surfaceView);
        cvCam = view.findViewById(R.id.cv_cam);
        tvNoMatch = view.findViewById(R.id.tv_no_match);
        clMath = view.findViewById(R.id.cl_match);

        tvDateHeure = view.findViewById(R.id.tv_date_heure);
        tvNomEquipe1 = view.findViewById(R.id.tv_nom_equipe1);
        tvNomEquipe2 = view.findViewById(R.id.tv_nom_equipe2);
        tvLabelEquipe1 = view.findViewById(R.id.tv_btn_label_equipe1);
        tvLabelEquipe2 = view.findViewById(R.id.tv_btn_label_equipe2);
        btnCoteEquipe1 = view.findViewById(R.id.btn_equipe1);
        btnCoteEquipe2 = view.findViewById(R.id.btn_equipe2);
        btnCoteNull = view.findViewById(R.id.btn_null);



    }

    private void listenerButtonMatch(Match match){
        btnCoteEquipe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pari pari = new Pari(match.getId(), match.getEquipe1(), SessionManager.getInstance(getContext()).getIdConnectedUser());
                AccueilFragment.showPariDialog(pari, match.getCoteEquipe1());
            }
        });

        btnCoteEquipe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pari pari = new Pari(match.getId(), match.getEquipe2(), SessionManager.getInstance(getContext()).getIdConnectedUser());
                AccueilFragment.showPariDialog(pari, match.getCoteEquip2());
            }
        });

        btnCoteNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pari pari = new Pari(match.getId(), null, SessionManager.getInstance(getContext()).getIdConnectedUser());
                AccueilFragment.showPariDialog(pari, match.getCoteMatchNull());
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getContext(), getResources().getText(R.string.scanne_qr_code), Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getContext(), getResources().getText(R.string.scanne_qr_code_arreter), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                                cvCam.setVisibility(View.GONE);
                                cameraSource.stop();
                        }
                    });
                }
            }
        });
    }






}