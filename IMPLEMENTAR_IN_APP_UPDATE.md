# 🔄 Implementar In-App Update (Atualização no App)

## 📋 O QUE É?

O Google Play In-App Update permite que você notifique os usuários sobre atualizações disponíveis diretamente dentro do app, sem precisar de notificações push ou servidor externo.

## 🎯 TIPOS DE ATUALIZAÇÃO:

### 1. **FLEXIBLE (Flexível)** - Recomendado
- Usuário pode continuar usando o app
- Download acontece em background
- Quando terminar, mostra um snackbar para instalar
- Melhor para atualizações não críticas

### 2. **IMMEDIATE (Imediata)** - Para atualizações críticas
- Tela de atualização em tela cheia
- Usuário não pode usar o app até atualizar
- Use apenas para correções críticas de segurança

---

## 🛠️ IMPLEMENTAÇÃO

### **PASSO 1: Adicionar dependência**

Edite `app/build.gradle.kts` e adicione:

```kotlin
dependencies {
    // ... suas dependências existentes ...
    
    // In-App Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
```

---

### **PASSO 2: Criar classe UpdateManager**

Crie o arquivo: `app/src/main/java/com/jaydev/awsquiz/UpdateManager.java`

```java
package com.jaydev.awsquiz;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.material.snackbar.Snackbar;

public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static final int REQUEST_CODE_UPDATE = 1001;
    
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    public UpdateManager(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
    }

    /**
     * Verifica se há atualização disponível e inicia o fluxo
     * @param updateType AppUpdateType.FLEXIBLE ou AppUpdateType.IMMEDIATE
     */
    public void checkForUpdate(int updateType) {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                
                Log.d(TAG, "Atualização disponível!");
                startUpdate(appUpdateInfo, updateType);
            } else {
                Log.d(TAG, "Nenhuma atualização disponível");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Erro ao verificar atualização", e);
        });
    }

    /**
     * Inicia o fluxo de atualização
     */
    private void startUpdate(AppUpdateInfo appUpdateInfo, int updateType) {
        try {
            if (updateType == AppUpdateType.FLEXIBLE) {
                setupFlexibleUpdateListener();
            }
            
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateType,
                activity,
                REQUEST_CODE_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Erro ao iniciar atualização", e);
        }
    }

    /**
     * Configura listener para atualização flexível
     */
    private void setupFlexibleUpdateListener() {
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompletedSnackbar();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    /**
     * Mostra snackbar quando download terminar
     */
    private void showUpdateCompletedSnackbar() {
        Snackbar snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "Atualização baixada! Reinicie o app para instalar.",
            Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction("REINICIAR", view -> appUpdateManager.completeUpdate());
        snackbar.show();
    }

    /**
     * Verifica se há atualização em andamento (para retomar após fechar o app)
     */
    public void resumeUpdateIfNeeded() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            // Se atualização foi baixada mas não instalada
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompletedSnackbar();
            }
            
            // Se atualização imediata foi iniciada mas não completada
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        REQUEST_CODE_UPDATE
                    );
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Erro ao retomar atualização", e);
                }
            }
        });
    }

    /**
     * Limpa recursos quando Activity for destruída
     */
    public void cleanup() {
        if (installStateUpdatedListener != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}
```

---

### **PASSO 3: Usar no MainActivity**

Edite `MainActivity.java`:

```java
package com.jaydev.awsquiz;

import android.content.Intent;
import android.os.Bundle;
// ... outros imports ...
import com.google.android.play.core.install.model.AppUpdateType;

public class MainActivity extends AppCompatActivity {

    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Inicializar UpdateManager
        updateManager = new UpdateManager(this);
        
        // Verificar atualização ao abrir o app
        // Use AppUpdateType.FLEXIBLE para atualização flexível
        // Use AppUpdateType.IMMEDIATE para atualização obrigatória
        updateManager.checkForUpdate(AppUpdateType.FLEXIBLE);
        
        // ... resto do código ...
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retomar atualização se necessário
        if (updateManager != null) {
            updateManager.resumeUpdateIfNeeded();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpar recursos
        if (updateManager != null) {
            updateManager.cleanup();
        }
    }
}
```

---

## 🎯 COMO USAR:

### **Para atualização FLEXÍVEL (recomendado):**
```java
updateManager.checkForUpdate(AppUpdateType.FLEXIBLE);
```

### **Para atualização IMEDIATA (crítica):**
```java
updateManager.checkForUpdate(AppUpdateType.IMMEDIATE);
```

---

## 🧪 COMO TESTAR:

### **Método 1: Internal App Sharing**
1. No Play Console, vá em **Internal app sharing**
2. Faça upload de uma versão com versionCode maior
3. Instale a versão antiga no dispositivo
4. Abra o link do Internal App Sharing
5. O app vai detectar a atualização

### **Método 2: Teste interno**
1. Publique versão antiga no teste interno
2. Instale no dispositivo
3. Publique versão nova no teste interno
4. Abra o app e verá a notificação de atualização

---

## 📊 CONFIGURAR PRIORIDADE DE ATUALIZAÇÃO

No workflow do GitHub Actions, você pode definir a prioridade:

```yaml
- name: Upload to Google Play
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT_JSON }}
    packageName: com.jaydev.awsquiz
    releaseFiles: app/build/outputs/bundle/release/app-release.aab
    track: internal
    status: completed
    inAppUpdatePriority: 5  # 0-5 (5 = alta prioridade)
```

**Prioridades:**
- **0-2:** Baixa (atualização flexível)
- **3-4:** Média (atualização flexível com mais insistência)
- **5:** Alta (considere usar atualização imediata)

---

## 🎨 PERSONALIZAR MENSAGENS

Você pode personalizar as mensagens editando o código:

```java
Snackbar snackbar = Snackbar.make(
    activity.findViewById(android.R.id.content),
    "Nova versão disponível com 10 simulados! 🎉",
    Snackbar.LENGTH_INDEFINITE
);
```

---

## ✅ VANTAGENS:

- ✅ Nativo do Google Play
- ✅ Não precisa de servidor
- ✅ Não precisa de Firebase
- ✅ Funciona automaticamente
- ✅ Usuário atualiza sem sair do app
- ✅ Controle de prioridade

---

## 🚀 PRÓXIMOS PASSOS:

1. Adicionar a dependência no `build.gradle.kts`
2. Criar a classe `UpdateManager.java`
3. Integrar no `MainActivity.java`
4. Testar com Internal App Sharing
5. Publicar nova versão!

---

**Quer que eu implemente isso no seu projeto agora?**
