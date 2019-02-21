# Practico Final de Tecnología de Software Base UTN-FRC :computer:
## Trabajo Práctico Único: Tabla Hash con Direccionamiento Abierto 
### Enunciado:

Siguiendo el modelo de implementación del concepto de *Tabla Hash* con Listas de Desborde, se solicita ahora implementar una clase ```TSB_OAHashtable``` (por _TSB Open Addressing Hash Table_), que represente una _Tabla Hash_ pero con estrategia de _Direccionamiento Abierto_ para la resolución de colisiones.

La clase debe ser implementada en forma rigurosa, siguiendo el modelo ya presentado para la clase ```TSBHashtable```. Esto implica:
- [x] Implementar la interface _Map<K, V>_ y desde ella, los mismos métodos que se implementaron para la clase ```TSBHashtable```.
- [x] Definir dentro de la clase ```TSB_OAHashtable``` una clase interna _Entry_ que implemente la interface _Map.Entry<K, V>_ para representar a cada par que se almacene en la tabla.
- [x] Definir dentro de la clase ```TSB_OAHashtable``` las tres clases internas para gestionar las vistas _stateless de claves_, de _valores_ y de _pares de la tabla_, incluyendo a su vez en ellas las clases internas para representar a los iteradores asociados a cada vista.
- [x] Redefinir en la clase ```TSB_OAHashtable``` los métodos _equals()_, _hashCode()_, _clone()_ y _toString()_ que se heredan desde _Object_.
- [x] Definir en la clase ```TSB_OAHashtable``` los métodos _rehash()_ y _contains(value)_ que no vienen especificados por _Map_, pero son especialmente propios de la clase (emulando a _java.util.Hashtable_).

La idea es tomar como modelo al que se presentó para la clase ```TSBHashtable``` en la Ficha 10, e implementar los mismos métodos publicos y protegidos de esa clase, más las clases internas citadas. Obviamente, con relación a los atributos privados y  métodos privados, cada grupo de trabajo hará su propia propuesta y diseño.

> Hasta acá consta el trabajo final, el resto es la continuación del enunciado pero se debe presentar solo la clase.

Luego de implementar la clase pedida, se debe desarrollar un programa que sea capaz de procesar un conjunto de _archivos de texto_ y construir una _tabla hash_ con todas las _palabras diferentes_ descubiertas en esos archivos, de forma de determinar además la _frecuencia de aparición_ de cada palabra en esos archivos. Para ello, el programa debe ser capaz de procesar de a uno los archivos indicados (puede descargarlos desde  [aquí](https://uv.frc.utn.edu.ar/pluginfile.php/213148/mod_assign/intro/Libros%20%5BTP%20Unico%20TSB%202014%5D.zip)), obteniendo de cada uno las distintas palabras detectadas. Notar aquí que para obtener las palabras se deberá analizar (o "parsear") cada documento, limpiar el texto de los signos de puntuación, palabras numéricas o alfanuméricas, dígitos y todo otro símbolo que no forme parte de una palabra normalmente entendida; y una vez realizado este trabajo (o a medida que se va realizando) se procese cada palabra agregándola a la tabla y contando su _frecuencia de aparición_.

Como se está trabajando con una tabla tipo _Map_, y considerando que luego se pedirá buscar una palabra e indicar cuántas veces apareció, se sugiere que en cada par _(key, value)_ que se almacene en la tabla el objeto _key_ sea la palabra detectada en el texto, y el objeto _value_ sea la _frecuencia de aparición_ de esa palabra.

### Requerimientos:

El programa a desarrollar debe cumplir con los siguientes requerimientos:

1. Contar con interfaz de usuario simple, pero basada en ventanas y componentes visuales en base al framework Java FX (que será oportunamente tratado en clases y con ficha de soporte).
2. Prestar especial atención en cuanto a factores de eficiencia:  pensar en un diseño eficiente para no caer en casos en los que el proceso de los archivos incurra en una cantidad exagerada de tiempo y vuelva nuestro programa una alternativa poco viable de ser utilizada en la realidad.
3. Permitir que la _tabla hash_, una vez generada, sea grabada en un archivo serializado. Y permitir recuperar esa tabla desde el archivo serializado. Esto puede hacerse en forma simple: al terminar de crear la tabla, grabarla por serialización en forma automática y dejarla allí. Y cada vez que el programa sea ejecutado, levantar la tabla también en forma automática desde el archivo serializado (a menos que el archivo no exista).
4. Mostrar en todo momento la cantidad total de palabras distintas que contiene la tabla. Permitir además, ingresar una palabra cualquiera en un campo de texto, buscar esa palabra en la tabla y mostrar su frecuencia de aparición.
5. Permitir agregar documentos nuevos.  Al presentar el trabajo, la tabla debe estar ya generada y serializada en base a los archivos de texto dados (cada alumno debe entregar el proyecto Java, y el archivo serializado que contiene a la tabla ya creada). Al volver a arrancar el programa, el mismo debe volver a levantar la tabla (como se indicó en el punto c). Pero el programa debe permitir que se procese uno o varios archivos de texto más se actualice la tabla con las nuevas frecuencias o nuevas palabras de acuerdo a el/los nuevos archivos procesados.
