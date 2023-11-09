# Riconoscimento-facciale

Questo progetto è stato pensato e realizzato da Andrea Lo Russo per il corso di Sistemi Digitali M tenuto dal Professor Stefano MAttoccia e dal Professor Matteo Poggi presso l'Università di Bologna.

L’obiettivo del progetto proposto è integrare un sistema di riconoscimento facciale all’interno di un dispositivo mobile basato su Android. Il sistema implementato permette, sotto determinate condizioni, di inserire una o pi`u immagini contenenti un volto che saranno utilizzate
come riferimento per il riconoscimento facciale. Il modello integrato nell'applicativo Android è stato implementato e addestrato da zero.


# Primo avvio e Fragment About
Al primo avvio è necessario fornire i permessi per l’utilizzo della fotocamera, ciò è reso possibile attraverso
una finestra di dialogo che consente di elargire o negare tale permesso. Al contrario, forniti i permessi, il primo fragment visualizzato è chiamato About e, come già anticipato nella sezione precedente, si tratta di un semplice fragment contenente informazioni di carattere generale e una guida rapida all’uso.

![about10](https://github.com/Andrisky/Riconoscimento-facciale/assets/46251425/6ed9b851-76d6-4fae-ae33-aad71bd67683)

# Fragment Settings
Per avere accesso a tutte le funzionalità dell’applicativo è necessario impostare un codice di sicurezza, ovvero un PIN. Impostato il codice di sicurezza il Fragment sarà arricchito da un ulteriore form, denominato Current PIN. Infatti, a patto di conoscere il PIN impostato, è possibile sostituirlo con un nuovo codice di sicurezza.

![settings10](https://github.com/Andrisky/Riconoscimento-facciale/assets/46251425/0c8512bd-dccc-4228-ad28-25c843c9347d)

# Fragment Camera

Il Fragment dedicato alla gestione della fotocamera permette di catturare e salvare frame, che saranno
in seguito utilizzati come immagini di riferimento per effettuare il riconoscimento facciale.  All’apertura,
il Fragment si presenta in maniera essenziale, infatti `e presente soltanto una View per la visualizzazione
dei frame acquisiti dalla fotocamera, che copre l’intera superficie del fragment, e da un Button di forma
circolare per catturare i frame desiderati. Il Frame catturato potrà essere salvato se  il codice di sicurezza immesso è corretto e se all'interno del frame è presente un numero di volti pari ad 1.

![camera](https://github.com/Andrisky/Riconoscimento-facciale/assets/46251425/72a7ad4a-e6a4-4a72-b8f4-0c02981201f8)

# Fragment Profiles
I frame salvati all’interno dell’applicazione sono visualizzati nel fragment Profiles mediante l’utilizzo di una RecyclerView. Ogni elemento della lista è cliccabile: il click produce l’apertura di una finestra di dialogo che consente la cancellazione del profilo previo inserimento del PIN.

![profiles10](https://github.com/Andrisky/Riconoscimento-facciale/assets/46251425/7a74f557-5fa3-4b81-8bd6-928590e5ff45)

# Ambiente simulato
Per testare il riconoscimento facciale è stato realizzato un ambiente che permette di simulare lo sblocco di un dispositvo Android.
Per la realizzazione dell'ambiente simulato sono stati utilizzati tre Fragment, uno per il riconoscimento facciale, uno per l’inserimento del PIN (in caso il riconoscimento facciale fallisse) e l’ultimo per simulare la pagina iniziale di un dispositivo dopo essere stato sbloccato.

![ambiente](https://github.com/Andrisky/Riconoscimento-facciale/assets/46251425/e54e9921-a1b2-4ef9-aef6-69db76c84c78)

