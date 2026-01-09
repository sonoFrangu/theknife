# TheKnife Project
---
## Requisiti di Sistema

Per l’utilizzo dell’applicazione è necessario disporre di un ambiente di esecuzione compatibile con **Java**.

L’applicazione richiede la presenza del **Java Runtime Environment (JRE)**, che consente l’esecuzione di programmi Java senza la necessità di strumenti di sviluppo.

## Requisiti software

- **Java Runtime Environment (JRE)** versione **21** o compatibile  
- **JavaFX**
- Sistema operativo compatibile con Java:
  - Windows
  - macOS
  - Linux

Una volta installato il JRE, l’applicazione può essere avviata eseguendo il file con estensione `.jar` senza ulteriori configurazioni su **Windows**.  
Per macOS fare riferimento alla sezione [Avviare l'applicazione con macOS](#avviare-lapplicazione-con-macos).

---

## Installazione del Programma

Per poter eseguire l’applicazione è necessario scaricare il software eseguibile sul proprio dispositivo seguendo questi passaggi:

1. Fare clic per scaricare la cartella compressa presente su **OneDrive**
2. Fare clic con il tasto destro sul file con estensione `.zip` e selezionare **Estrai i file...**, quindi scegliere il percorso desiderato  
   - In alternativa è possibile utilizzare software come **WinRAR** per l'estrazione

---

## Esecuzione e Utilizzo

### Avviare l'applicazione

In base al sistema operativo utilizzato, seguire le istruzioni riportate di seguito.

---

## Windows

### Prima alternativa

È sufficiente fare doppio clic sul file:

```
avvioWindows.bat
```

presente nella cartella principale scelta durante l'installazione.

### Seconda alternativa (da terminale)

1. Spostarsi nel percorso in cui è stato scaricato il file `.jar`:
   ```bash
   cd [percorso]
   ```
2. Digitare:
   ```bash
   javaw -jar "TheKnife-4.0.jar"
   ```
3. L'applicazione verrà avviata

---

## Avviare l'applicazione con macOS

### Prima alternativa

Prima di avviare l'applicazione è necessario eseguire il seguente passaggio **una sola volta**:

```bash
chmod +x AvvioMacOS.command
```

> È possibile trascinare il file `AvvioMacOS.command` direttamente nel terminale dopo aver digitato `chmod +x `

Successivamente, per avviare l'applicazione è sufficiente fare doppio clic su:

```
avvioMacOS.command
```

presente nella cartella principale.

### Seconda alternativa (da terminale)

1. Spostarsi nel percorso in cui è stato scaricato il file `.jar`:
   ```bash
   cd [percorso]
   ```
2. Digitare:
   ```bash
   java -jar TheKnife-4.0.jar
   ```
3. L'applicazione verrà avviata

