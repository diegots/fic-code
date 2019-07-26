# Objetivos del proyecto

1. Manejo autónomo del algoritmo de recomendación sin necesidad de utilizar en ningún momento la consola de AWS.
2. Superar unos mínimos respecto a la calidad visual de la web. Esto se logra por medio del uso del sistema de plantillas de Django y el uso de Bootstrap.
3. No se aborda el despliegue y configuración sobre un entorno de producción con Apache, SSL, MariaDB, etc.
5. No obstante, la aplicación final debe ser fácilmente desplegable siguiendo los pasos del manual de instalación proporcionado.

# Instalación

## Entorno utilizado
- Ubuntu 18.04
- Python 3.6.7
- Django 2.2

## Descarga del software
1. Instalar ```pip3```: ```sudo apt-get install python3-pip```.
2. Instalar ```virtualenv```: ```pip3 install --user virtualenv```. Esta orden instala la herramienta en ```~/.local/bin```. Puede ser necesario cerrar la sesión y volver a entrar para que esta ruta sea añadida al PATH. Alternativamente se puede actualizar el PATH sin necesidad de cerrar la sesión, usando ```source ~/.profile```.
3. Clonar el repositorio del proyecto desde Github, mediante SSH, ```git clone git@github.com:diegots/fic-tfg-django-web.git```, o bien HTTPS, ```git clone https://github.com/diegots/fic-tfg-django-web.git```.
4. Navegar al directorio del proyecto: ```cd fic-tfg-django-web/```.
5. Crear un nuevo entorno virtual, ```virtualenv env``` y activarlo: ```source env/bin/activate```.
6. Instalar Django 2.2: ```pip3 install 'django==2.2'```.
7. (Opcional) Para comprobar que Django está correctamente instalado se puede ejecutar: ```python -m django --version```, lo cual deberá mostrar el número de versión instalado o un error en caso de que algo haya salido mal.

Ya se puede desactivar el entorno virtual: ```deactivate```.

## Configuración
Es necesario configurar varios parámetros para la correcta ejecución de la aplicación. Los ficheros a editar son:
- ```scripts/active-python-env.sh```
    - ```DJANGO_ROOT_DIR``` en la variable  hay que indicar la ruta al directorio del proyecto. 
    - ```VIRTUALENV_DIR_NAME``` en la variable  hay que indicar el nombre utilizado para el directorio del entorno virtual.
- ```scripts/global-vars.sh```. En este fichero debe definirse:
    - ```TFG_BUCKET_NAME``` nombre del bucket S3 que se desea utilizar.
    - ```TFG_SECRET_KEY``` clave utilizada por Django para garantizar la seguridad del servicio.
    - ```TFG_SSH_KEY``` nombre de la clave SSH utilizada para iniciar sesión en las máquinas del cluster.
    - (Opcional) ```TFG_ALLOWED_HOSTS``` direcciones IP admitidas por el servidor web de Django.
    - (Opcional) ```TFG_DEBUG``` activa las funciones de depuración del servicio web.

## Preparar la base de datos
La creación y configuración de la base de datos se realiza con dos comandos:
1. ```source scripts/active-python-env.sh```
2. ```python manage.py migrate```

El primero activa el entorno virtual recién creado y el segundo genera la BD.

## Crear un usuario administrador
Para crear un primer usuario, se utiliza ```manage.py```: 
```python manage.py createsuperuser --username=joe --email=joe@example.com```

A partir de ahí la consola de administración de Django está accesible en <http://127.0.0.1:8000/admin/>

Para finalizar el entorno virtual se puede cerrar la consola o bien usar ```deactivate```. Con este paso se finaliza la instalación.

# Inicio normal del servicio
Para iniciar el servicio hechos los pasos anteriorres, basta con ejecutar:
- ```source scripts/active-python-env.sh``` y
- ```python manage.py runserver```
El servidor web escucha en <http://127.0.0.1:8000/>, o en la IP indicada. Se puede detener en cualquier momento con ```Ctrl-c```.
