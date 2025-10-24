package com.example.idcma_project_prm392.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public final class FirebaseUtils {
    private FirebaseUtils() {}

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseStorage storage() {
        return FirebaseStorage.getInstance();
    }

    public static FirebaseUser currentUser() {
        return auth().getCurrentUser();
    }

    public static Task<AuthResult> signIn(String email, String password) {
        return auth().signInWithEmailAndPassword(email, password);
    }

    public static Task<AuthResult> register(String email, String password) {
        return auth().createUserWithEmailAndPassword(email, password);
    }

    public static void signOut() {
        auth().signOut();
    }

    public static CollectionReference userCertificates(String userId) {
        return db().collection("users").document(userId).collection("certificates");
    }

    public static Task<QuerySnapshot> fetchCertificates(String userId) {
        return userCertificates(userId).get();
    }

    public static DocumentReference certificateDoc(String userId, String certificateId) {
        return userCertificates(userId).document(certificateId);
    }

    public static Task<Void> deleteCertificate(String userId, String certificateId) {
        return certificateDoc(userId, certificateId).delete();
    }
}
