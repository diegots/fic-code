- Corregir paleta de colores en los dibujos
	1. Correspondencia de coordenadas
	2. Secciones de un PDF
	3. Transformaciones en el espacio de Hough
	4. Jerarquía de Chomsky. 4 fondos. Blanco y negro texto.
	5. Ejemplo del proceso de an�lisis del compilador 2, texto y flechas.
	6. Ciclo de vida de Scrum (todo en negro)
	7. Casos de uso de software. verde �rboles
	8. Visión general del sistema, etapas. Negro verde.
	9. Casos del algoritmo de selección de regiones. 2 regiones


staging-playbook.yml
---
- name: deploy project to staging
  vars:
    app_dir: solcoerp
    repo_dir: solcoerp-repo
    deploy_dir: deploy
    input_dir: input
  vars_files:
    - svn-credentials.yml
  hosts: backendservers
  remote_user: solcoerp-deploy
  gather_facts: false
  tasks:
    - name: Remove old deploy directory
      file:
        path: "{{ app_dir }}"
        state: absent

    - name: Check out latest version from SVN
      subversion:
        repo: "{{ repo_url }}"
        dest: "{{ repo_dir }}"
        username: "{{ username }}"
        password: "{{ password }}"

    - name: Call Makefile deploy task
      make:
        chdir: "{{ repo_dir }}"
        target: deploy

    - name: Create a root dir for the app
      file:
        path: "{{ app_dir }}"
        state: directory

    - name: Create input directory inside root one
      file:
        path: "{{ app_dir  }}/{{ input_dir }}"
        state: directory

    - name: Copy deploy dir as app dir
      copy:
        src: "$HOME/{{ repo_dir }}/{{ deploy_dir }}/"
        dest: "$HOME/{{ app_dir }}/app"
        remote_src: yes

    - name: Remove repo directory
      file:
        path: "{{ repo_dir }}"
        state: absent


/home/diego/win/2.sync/static/grado-en-informática/4to-trabajo-fin-grado/galiasdoc-org/2020-01-enero/01-repositorios-iniciales/solcoerp-git-1/doc/ansible/ansible-ping.yml
/home/diego/win/2.sync/static/grado-en-informática/4to-trabajo-fin-grado/galiasdoc-org/2020-01-enero/01-repositorios-iniciales/solcoerp-git-2/ansible/deploy-playbook.yml
/home/diego/win/2.sync/static/grado-en-informática/4to-trabajo-fin-grado/galiasdoc-org/2020-01-enero/01-repositorios-iniciales/solcoerp-git-2/ansible/svn-credentials.yml
/home/diego/win/2.sync/static/grado-en-informática/4to-trabajo-fin-grado/SOLCOerp/tags/release-0.1-solco/ansible/staging-playbook.yml


El funcionamiento de alto nivel es como sigue: se invoca el método \verb|main()|, que inicializa la configuración de la aplicación y también las variables globales. Después se gestionan los argumentos de entrada, tras lo cual se carga la librería dinámica que contiene la función \verb|yyparse()|, a la cual se llama, dando comienzo al proceso de parsing. Al finalizar se libera la memoria utilizada.

El proceso del parser conlleva varios pasos. En el primer paso se tokeniza la entrada, el lenguaje intermedio en este caso, aplicando las reglas léxicas y se evalúan las sintácticas de tal manera que se va construyendo el \acrlong{ast}. Si no se produce ningún error, se consume la entrada y se llega al axioma, donde se llama a las funciones de generación de código, pasando el árbol. Las funciones de generación recorren el \acrshort{ast} creando el texto que se volcará finalmente a disco para cada uno de los ficheros que componen la salida.

textract-y-document-ai

+Dos servicios diferentes a los productos anteriores pero aplicables al problema son, Textract de Amazon \cite{solucionesComerciales_amazon_textract} y Document AI de Google \cite{solucionesComerciales_google_documentAI}. Ninguno de los dos suporta el flujo de informaciÃ³n explicado ni estÃ¡n pensados para ser soluciÃ³n para el usuario final. Lo que ofrecen es un \emph{\acrlong{api}} capaz de recibir documentos y generar informaciÃ³n estructurada como salida. El caso de Textract es totalmente opaco y por tanto no configurable. La salida consiste en ficheros JSON donde puede haber varios tipos de objetos: pÃ¡ginas, lÃ­neas y palabras, informaciÃ³n de formularios (pares clave-valor), tablas, y elementos seleccionables como casillas. AdemÃ¡s es capaz de identificar notas manuscritas. El servicio de Google permite definir \emph{processors}, que son plantillas especÃ­ficas para modelos de documentos concretos. Actualmente parece que el servicio es muy reciente y estÃ¡ mayormente en beta. Cualquiera de ellos podrÃ­a utilizarse para construir una soluciÃ³n mÃ¡s completa. Como otros servicios en la nube, el coste depende de la carga de trabajo procesada.

