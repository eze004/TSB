package clases;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable {

    /*
     * PRIMERO
     * 
     * Implementar la interface Map<K, V> y desde ella, los mismos métodos que se
     * implementaron para la clase TSBHashtable.
     */

    // el tamaño máximo que podrá tener el arreglo de soprte...
    private final static int MAX_SIZE = Integer.MAX_VALUE;

    // El array hash
    private Entry<K, V> table[];
    
    /**
     * Como mejora se me propuso que en vez de manejar los estados con:
     * 0: Abierta
     * 1: Cerrada
     * 2: Tumba
     * creara variables finales staticas que ya tuvieran ese valor,
     * de esa forma el codigo seria mas legible.
     * 
     * Otras porupuestas:
     * 1) En vez de usar un array de int's utilizar un Enum
     * (Esto traeria mayor uso de la memoria por lo cual no es muy recomendable)
     * 
     * 2) Se podria agregar un atributo mas a Entry que fuera estado,
     * pero esto requeriria un desarrollo extra que puede no terminar siendo  practico o de buen rendimiento.
     */
    private int states[];

    // el tamaño inicial de la tabla (tamaño con el que fue creada)...
    private int initial_capacity;

    // la cantidad de objetos que contiene la tabla...
    private int count;

    // el factor de carga para calcular si hace falta un rehashing...
    private float load_factor;


    // conteo de operaciones de cambio de tamaño (fail-fast iterator).
    protected transient int modCount;




    // Constructores

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 53 y con factor de
     * carga igual a 0.75f.
     */
    public TSB_OAHashtable() {
        this(53, 0.75f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor de carga
     * igual a 0.75f.
     * 
     * @param initial_capacity la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity) {
        this(initial_capacity, 0.75f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor de
     * carga indicado. Si la capacidad inicial indicada por initial_capacity es
     * menor o igual a 0, la tabla será creada de tamaño 53. Si el factor de carga
     * indicado es negativo o cero, se ajustará a 0.75f.
     * 
     * @param initial_capacity la capacidad inicial de la tabla.
     * @param load_factor      el factor de carga de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity, float load_factor) {
        if (load_factor <= 0) {
            load_factor = 0.75f;
        }
        if (initial_capacity <= 0) {
            initial_capacity = 53;
        } else {
            if (initial_capacity > TSB_OAHashtable.MAX_SIZE) {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            } else {
                initial_capacity = this.siguientePrimo(initial_capacity);
            }
        }

        // Se crea la tabla de Entry
        this.table = new Entry[initial_capacity];

        // Inicializo el vector de estados
        states = new int[initial_capacity];

        /**
         * Esto no haria falta ya que en la inicializacion del vector int[]
         * Java ya setea a todos los valores en 0
         */
        for (int i = 0; i < states.length; i++) {
            states[i] = 0;
        }

        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * 
     * @param t el Map a partir del cual se creará la tabla.
     */
    public TSB_OAHashtable(Map<? extends K, ? extends V> t) {
        this(53, 0.75f);
        this.putAll(t);
    }

    // /los métodos especificados por Map.

    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     * 
     * @return la cantidad de elementos de la tabla.
     */
    @Override
    public int size() {
        return this.count;
    }

    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     * 
     * @return true si la tabla está vacía.
     */
    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }

    /**
     * Determina si la clave key está en la tabla.
     * 
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */
    @Override
    public boolean containsKey(Object key) {
        return (this.get((K) key) != null);
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que entra
     * como parámetro. Equivale a contains().
     * 
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null si
     * la tabla no contiene ningún objeto asociado a esa clave.
     * 
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o
     *         null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException   si la clase de key no es compatible con la
     *                              tabla.
     */
    @Override
    public V get(Object key) {
        if (key == null)
            throw new NullPointerException("get(): parámetro null");

        int ih = this.h((K)key);
        int ic = ih;
        int j = 1;
        V valueReturn = null;

        // Busco el elemento a encontrar
        while (this.states[ic] != 0) {
            // Si en la posicion actual esta cerrada verifico si es el mismo
            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];

                // Si es el mismo devuelvo el value
                if(key.equals(entry.getKey())){
                    valueReturn = entry.getValue();
                    return valueReturn;
                }
            }

            // Calculo el nuevo indice
            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }

        // Si la clave no estaba asociada devolvemos null
        return valueReturn;
    }

    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en esta
     * tabla. Si la tabla contenía previamente un valor asociado para la clave,
     * entonces el valor anterior es reemplazado por el nuevo (y en este caso el
     * tamaño de la tabla no cambia).
     * 
     * @param key   la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya estaba
     *         asociada con alguno, o null si la clave no estaba antes asociada a
     *         ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException("put(): parámetro null");

        int ih = this.h(key);
        int ic = ih;
        int first_tombstone = -1;
        int j = 1;
        V old = null;

        // Verifico que no exista previamente y guardo first_tombstone
        while (this.states[ic] != 0) {

            // Si en la posicion actual esta cerrada verifico si es el mismo
            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];
                // Si es el mismo lo piso y devuelvo el value viejo
                if(key.equals(entry.getKey())){
                    old = entry.getValue();
                    entry.setValue(value);
                    this.count++;
                    this.modCount++;

                    return old;
                }
            }

            //Si en el camino encuentro un indice tumba lo tengo en cuenta
            if(this.states[ic] == 2 && first_tombstone < 0) first_tombstone = ic;

            // Calculo el nuevo indice
            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }

        // Si tenemos un indice first_tombstone es mejor que el iCuadrtico
        if (first_tombstone >= 0) ic = first_tombstone;
        
        // Si esta abierto o tumba
        this.table[ic] = new Entry<K, V>(key, value);
        this.states[ic] = 1;

        // Sumo el contador
        this.count++;
        this.modCount++;

        // Verifico el factor de carga
        float fc = (float) count / (float) this.table.length;
        if (fc >= this.load_factor)
            this.rehash();

        return old;
    }

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado). El
     * método no hace nada si la clave no está en la tabla.
     * 
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     *         estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */
    @Override
    public V remove(Object key) {
        if (key == null)
            throw new NullPointerException("remove(): parámetro null");

        int ih = this.h((K)key);
        int ic = ih;
        int j = 1;
        V old = null;

        // Busco el elemento a eliminar
        while (this.states[ic] != 0) {

            // Si en la posicion actual esta cerrada verifico si es el mismo
            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];

                // Si es el mismo lo elimino y devuelvo el value
                if(key.equals(entry.getKey())){
                    old = entry.getValue();
                    this.table[ic] = null;
                    this.states[ic] = 2;
                    
                    this.count--;
                    this.modCount++;

                    return old;
                }
            }

            // Calculo el nuevo indice
            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }

        // Si la clave no estaba asociada devolvemos null
        return old;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado. Los
     * nuevos objetos reemplazarán a los que ya existan en la tabla asociados a las
     * mismas claves (si se repitiese alguna).
     * 
     * @param m el map cuyos objetos serán copiados en esta tabla.
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina todo el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, el arreglo de soporte vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */
    @Override
    public void clear() {

        // Se recrea la tabla de Entry
        this.table = new Entry[this.initial_capacity];

        // Inicializo el vector de estados
        states = new int[this.initial_capacity];

        /**
         * Esto no haria falta ya que en la inicializacion del vector int[]
         * Java ya setea a todos los valores en 0
         */
        for (int i = 0; i < states.length; i++) {
            states[i] = 0;
        }

        this.count = 0;
        this.modCount++;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo que
     * los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando sobre
     * el conjunto vista, el resultado de la iteración será indefinido (salvo que la
     * modificación sea realizada por la operación remove() propia del iterador, o
     * por la operación setValue() realizada sobre una entrada de la tabla que haya
     * sido retornada por el iterador). El conjunto vista provee métodos para
     * eliminar elementos, y esos métodos a su vez eliminan el correspondiente par
     * (key, value) de la tabla (a través de las operaciones Iterator.remove(),
     * Set.remove(), removeAll(), retainAll() y clear()). El conjunto vista no
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará una
     * UnsuportedOperationException).
     * 
     * @return un conjunto (un Set) a modo de vista de todas las claves mapeadas en
     *         la tabla.
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            // keySet = Collections.synchronizedSet(new KeySet());
            keySet = new KeySet();
        }
        return keySet;
    }

    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la tabla,
     * por lo que los cambios realizados en la tabla serán reflejados en la
     * colección, y viceversa. Si la tabla es modificada mientras un iterador está
     * actuando sobre la colección vista, el resultado de la iteración será
     * indefinido (salvo que la modificación sea realizada por la operación remove()
     * propia del iterador, o por la operación setValue() realizada sobre una
     * entrada de la tabla que haya sido retornada por el iterador). La colección
     * vista provee métodos para eliminar elementos, y esos métodos a su vez
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Collection.remove(), removeAll(), removeAll(),
     * retainAll() y clear()). La colección vista no soporta las operaciones add() y
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * 
     * @return una colección (un Collection) a modo de vista de todas los valores
     *         mapeados en la tabla.
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo que
     * los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando sobre
     * el conjunto vista, el resultado de la iteración será indefinido (salvo que la
     * modificación sea realizada por la operación remove() propia del iterador, o
     * por la operación setValue() realizada sobre una entrada de la tabla que haya
     * sido retornada por el iterador). El conjunto vista provee métodos para
     * eliminar elementos, y esos métodos a su vez eliminan el correspondiente par
     * (key, value) de la tabla (a través de las operaciones Iterator.remove(),
     * Set.remove(), removeAll(), retainAll() and clear()). El conjunto vista no
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará una
     * UnsuportedOperationException).
     * 
     * @return
     * 
     * @return un conjunto (un Set) a modo de vista de todos los objetos mapeados en
     *         la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            // entrySet = Collections.synchronizedSet(new EntrySet());
            entrySet = new EntrySet();
        }
        return entrySet;
    }






































    /*
     * SEGUNDO
     * 
     * Definir dentro de la clase TSB_OAHashtable una clase interna Entry que
     * implemente la interface Map.Entry<K, V> para representar a cada par que se
     * almacene en la tabla.
     */
    private class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }






























    /*
     * TERCERO
     * 
     * Definir dentro de la clase TSB_OAHashtable las tres clases internas para
     * gestionar las vistas stateless de claves, de valores y de pares de la tabla,
     * incluyendo a su vez en ellas las clases internas para representar a los
     * iteradores asociados a cada vista. KeySet, EntrySet y ValueCollection
     */

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

    // 1 - KeySet
    private class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return (TSB_OAHashtable.this.remove(o) != null);
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class KeySetIterator implements Iterator<K> {

            // índice del entry anterior (si se requiere en remove())...
            private int last_entry;

            // índice del elemento actual en el iterador (el que fue retornado
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el mecanismo
             * fail-fast.
             */
            public KeySetIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya sido retornado
             * por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                if(current_entry >= t.length) { return false; }

                // busco el siguiente indice cerrado
                int next_entry = current_entry + 1;
                for (int i = next_entry ; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }

                // Si no encontro ninguno retorno false
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public K next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                // busco el siguiente indice cerrado
                int next_entry = current_entry;
                for (next_entry++ ; s[next_entry] != 1; next_entry++);

                // Actualizo los indices
                last_entry = current_entry;
                current_entry = next_entry;
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                
                // y retornar la clave del elemento alcanzado...
                K key = t[current_entry].getKey();

                return key;
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la posición
             * anterior al que fue removido. El elemento removido es el que fue retornado la
             * última vez que se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                TSB_OAHashtable.this.table[current_entry] = null;
                TSB_OAHashtable.this.states[current_entry] = 2;

                // queda apuntando al anterior al que se retornó...
                current_entry = last_entry;

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    // 2 - EntrySet
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }

        /*
         * Verifica si esta vista (y por lo tanto la tabla) contiene al par que entra
         * como parámetro (que debe ser de la clase Entry).
         */
        @Override
        public boolean contains(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            // variable auxiliar t y s para simplificar accesos...
            Entry<K, V> t[] = TSB_OAHashtable.this.table;
            int s[] = TSB_OAHashtable.this.states;

            Entry<K, V> entry = (Entry<K, V>) o;
            
            //Tomo el primer indice
            int ih = TSB_OAHashtable.this.h(entry.getKey());
            int ic = ih;
            int j = 1;

            // Busco el Entry
            while (s[ic] != 0) {
                // Si la posicion actual esta cerrada verifico si es el mismo
                if (s[ic] == 1) {
                    Entry<K, V> entryTable = t[ic];
                    
                    // Si es el mismo retorno true
                    if(entryTable.equals(entry)) return true;
                }

                //Si en el camino encuentro un indice tumba sigo buscando

                // Calculo el nuevo indice
                ic += j * j;
                j++;
                if (ic >= t.length) {
                    ic %= t.length;
                }
            }
            
            // Si no se encontro nada retorno false
            return false;
        }

        /*
         * Elimina de esta vista (y por lo tanto de la tabla) al par que entra como
         * parámetro (y que debe ser de tipo Entry).
         */
        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("remove(): parámetro null");
            }
            if (!(o instanceof Entry)) {
                return false;
            }

            // variable auxiliar t y s para simplificar accesos...
            Entry<K, V> t[] = TSB_OAHashtable.this.table;
            int s[] = TSB_OAHashtable.this.states;

            Entry<K, V> entry = (Entry<K, V>) o;


            //Tomo el primer indice
            int ih = TSB_OAHashtable.this.h(entry.getKey());
            int ic = ih;
            int j = 1;

            // Busco el elemento a eliminar
            while (s[ic] != 0) {

                // Si en la posicion actual esta cerrada verifico si es el mismo
                if (s[ic] == 1) {
                    Entry<K, V> entryTable = t[ic];

                    // Si es el mismo lo elimino y devuelvo true
                    if(entryTable.equals(entry)){
                        t[ic] = null;
                        s[ic] = 2;
                        
                        TSB_OAHashtable.this.count--;
                        TSB_OAHashtable.this.modCount++;

                        return true;
                    }
                }

                // Calculo el nuevo indice
                ic += j * j;
                j++;
                if (ic >= t.length) {
                    ic %= t.length;
                }
            }

            // Si la clave no estaba asociada devolvemos false
            return false;
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
            // índice del entry anterior (si se requiere en remove())...
            private int last_entry;

            // índice del elemento actual en el iterador (el que fue retornado
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el mecanismo
             * fail-fast.
             */
            public EntrySetIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya sido retornado
             * por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                if(current_entry >= t.length) { return false; }

                // busco el siguiente indice cerrado
                int next_entry = current_entry + 1;
                for (int i = next_entry ; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }

                // Si no encontro ninguno retorno false
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public Entry<K, V> next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                // busco el siguiente indice cerrado
                int next_entry = current_entry;
                for (next_entry++ ; s[next_entry] != 1; next_entry++);

                // Actualizo los indices
                last_entry = current_entry;
                current_entry = next_entry;
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                
                // y retornar el entry alcanzado...
                return t[current_entry];
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la posición
             * anterior al que fue removido. El elemento removido es el que fue retornado la
             * última vez que se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                TSB_OAHashtable.this.table[current_entry] = null;
                TSB_OAHashtable.this.states[current_entry] = 2;

                // queda apuntando al anterior al que se retornó...
                current_entry = last_entry;

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    // ValueCollection
    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsValue(o);
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class ValueCollectionIterator implements Iterator<V> {
            // índice del entry anterior (si se requiere en remove())...
            private int last_entry;

            // índice del elemento actual en el iterador (el que fue retornado
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el mecanismo
             * fail-fast.
             */
            public ValueCollectionIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya sido retornado
             * por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                if(current_entry >= t.length) { return false; }

                // busco el siguiente indice cerrado
                int next_entry = current_entry + 1;
                for (int i = next_entry ; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }

                // Si no encontro ninguno retorno false
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public V next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t y s para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;
                            int s[] = TSB_OAHashtable.this.states;

                // busco el siguiente indice cerrado
                int next_entry = current_entry;
                for (next_entry++ ; s[next_entry] != 1; next_entry++);

                // Actualizo los indices
                last_entry = current_entry;
                current_entry = next_entry;
                
                // avisar que next() fue invocado con éxito...
                next_ok = true;
                
                // y retornar la clave del elemento alcanzado...
                V value = t[current_entry].getValue();

                return value;
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la posición
             * anterior al que fue removido. El elemento removido es el que fue retornado la
             * última vez que se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                TSB_OAHashtable.this.table[current_entry] = null;
                TSB_OAHashtable.this.states[current_entry] = 2;

                // queda apuntando al anterior al que se retornó...
                current_entry = last_entry;

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }




















































    /**
     * CUARTO Redefinir en la clase TSB_OAHashtable los métodos equals(),
     * hashCode(), clone() y toString() que se heredan desde Object.
     */

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     * 
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }

        Map<K, V> t = (Map<K, V>) obj;
        if (t.size() != this.size()) {
            return false;
        }

        try {
            Iterator<Map.Entry<K, V>> i = this.entrySet.iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (t.get(key) == null) {
                    return false;
                } else {
                    if (!value.equals(t.get(key))) {
                        return false;
                    }
                }
            }
        }

        /**
         * Nunca se deberia capturar un NullPointerException porque eso significa
         * que hay un error en la logica del codigo.
         */
        catch (ClassCastException e) {
            return false;
        }

        return true;
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode()
    {
        if(this.isEmpty()) return 0;

        /**
         * En este caso es recomendable usar la funcion Arrays.hashCode porque
         * garantiza un hash unico para cada array.
         * Si se usa la suma, los objetos "ab" y "ba" tendrian el mismo hash.
         */
        return Arrays.hashCode(this.table);
    }

    /**
     * Devuelve el contenido de la tabla en forma de String.
     * 
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString() {
        StringBuilder cad = new StringBuilder("");
        cad.append("\nTabla: {\n");
        for (int i = 0; i < this.table.length; i++) {
            if(this.table[i] == null){
                cad.append("\t()\n");
            }else{
                cad.append("\t").append(this.table[i].toString()).append("\n");
            }
        }
        cad.append("}");
        return cad.toString();
    }

    /**
     * Retorna una copia superficial de la tabla.
     * 
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        // Se crea una nueva instancia de TSB_OAHashtable
        TSB_OAHashtable<K, V> t = new TSB_OAHashtable<>(this.table.length, this.load_factor);

        // copio todos los elementos
        for(Map.Entry<K, V> entry : this.entrySet()){
            t.put(entry.getKey(), entry.getValue());
        }

        return t;
    }




















































    /**
     * QUINTO Definir en la clase TSB_OAHashtable los métodos rehash() y
     * contains(value) que no vienen especificados por Map, pero son especialmente
     * propios de la clase (emulando a java.util.Hashtable).
     */

    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca
     * automaticamente cuando se detecta que la capacidad supera a cierto 
     * valor critico dado por (load_factor).
     */
    protected void rehash()
    {
        int old_length = this.table.length;

        // nuevo tamaño: el siguiente primo mayor al doble del tamanio actual...
        int new_length = siguientePrimo(old_length * 2 + 1);

        // no permitir que la tabla tenga un tamaño mayor al límite máximo...
        // ... para evitar overflow y/o desborde de índices...
        if(new_length > TSB_OAHashtable.MAX_SIZE)
            new_length = TSB_OAHashtable.MAX_SIZE;

        // crear el nueva tabla con new_length y estados
        Entry<K, V> tempTable[] = new Entry[new_length];
        int tempStates[] = new int[new_length];

        // Inicializo los estados
        for (int i = 0; i < tempStates.length; i++) tempStates[i] = 0;

        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;

        // recorrer el viejo arreglo y redistribuyo los objetos que tenia...
        for(int i = 0; i < this.table.length; i++){
            if(this.states[i] == 1){

                // obtengo un objeto cerrado de la vieja lista...
                Entry<K, V> x = this.table[i];

                // obtengo su nuevo valor de dispersión para el nuevo arreglo...
                K key = x.getKey();
                int y = this.h(key, tempTable.length);
                int ic = y, j = 1;

                /**
                 * Si bien, se puede demostrar que si el tamaño de la tabla es un número primo 
                 * y el porcentaje de ocupación no es mayor al 50% de la tabla
                 * la exploración cuadrática garantiza que la clave será insertada, 
                 * deberiamos controlar que no haya desbordamiento en la nueva tabla del rehash
                 * */
                while (tempStates[ic] != 0) {
                    // Calculo el nuevo indice en base a la nueva tabla
                    ic += j * j;
                    j++;
                    if (ic >= tempTable.length) {
                        ic %= tempTable.length;
                    }
                }

                // Se inserta en el nuevo arreglo
                tempTable[ic] = x;
                tempStates[ic] = 1;
            }
        }

        // cambiar la referencia table para que apunte a temp...
        this.table = tempTable;
        this.states = tempStates;
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    public boolean contains(Object value) {
        if (value == null)
            return false;

        Iterator<Map.Entry<K, V>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (value.equals(entry.getValue()))
                return true;
        }

        return false;
    }

    /*
     * Función hash. Toma una clave entera k y calcula y retorna un índice válido
     * para esa clave para entrar en la tabla.
     */
    private int h(int k) {
        return h(k, this.table.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y retorna
     * un índice válido para esa clave para entrar en la tabla.
     */
    private int h(K key) {
        return h(key.hashCode(), this.table.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de
     * tabla t, y calcula y retorna un índice válido para esa clave dedo ese tamaño.
     */
    private int h(K key, int t) {
        return h(key.hashCode(), t);
    }

    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y
     * retorna un índice válido para esa clave dado ese tamaño.
     */
    private int h(int k, int t) {
        if (k < 0)
            k *= -1;
        return k % t;
    }

    /**
     * Calcula el siguiente primo entero a partir de un numero dado.
     * @param n Numero entero a evaluar el siguiente Primo.
     * @return El siguiente numero primo a n.
     */
    private int siguientePrimo(int n)
    {
        if (n%2 == 0) n++;
        for (; !esPrimo(n); n+=2);
        return n;
    }

    /**
     * Evalua si el numero entero ingresado es un numero primo
     * @param n Numero entero a evaluar si cumple la condicion de primo.
     * @return True si n es Primo. Flase si no lo es.
     */
    private boolean esPrimo(int n)
    {
        for (int i = 3; i < (int) Math.sqrt(n); i+=2) {
            if (n%i == 0) return false;
        }
        return true;
    }
}
