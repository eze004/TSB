package clases;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * Una clase para emular el concepto de lista implementada sobre un arreglo, tal
 * como la clase java.util.ArrayList de Java (y al estilo de la clase List de 
 * Python). Se aplícó una estrategia de desarrollo basada en emular en todo lo
 * posible el comportamiento de la clase Java.util.ArrayList tal como se define
 * en la documentación javadoc de la misma, pero sin entrar en el código fuente
 * original (o sea, una estrategia de desarrollo tipo "clean room": se puede
 * analizar la documentación y los requerimientos, pero no el código fuente 
 * fuente ya existente). 
 * 
 * En esta segunda versión (y definitiva) la clase TSBArrayList deriva de la
 * clase AbsttactList (tal como java.util.ArrayList) e implementa las mismas
 * interfaces que implementa java.util.ArrayList. Se siguen aquí todas las 
 * recomendaciones de implementación disponibles en la documentación javadoc de
 * la clase AbstractList. 
 * 
 * @author Ing. Valerio Frittelli - Ing. Felipe Steffolani.
 * @version Agosto de 2017 - Version 2.0 (final).
 * @param <E> la clase cuyos objetos serán admisibles para la lista.
 */
public class TSBArrayList<E> extends AbstractList<E> 
             implements List<E>, RandomAccess, Cloneable, Serializable
{
    // el arreglo que contendrá los elementos...
    private Object[] items;
    
    // el tamaño inicial del arreglo...
    private int initial_capacity;
    
    // la cantidad de casillas realmente usadas...
    private int count;

    /**
     * Crea una lista con capacidad inicial de 10 casilleros, pero ninguno
     * ocupado realmente: la lista está vacía a todos los efectos prácticos. 
     * Este constructor es sugerido desde la documentación de la clase 
     * AbstractList.
     */
    public TSBArrayList()
    {
        this(10);
    }
    
    /**
     * Crea una lista conteniendo los elementos de la colección que viene como
     * parámetro, en el orden en que son retornados por el iterador de esa
     * colección. Si parámetro c es null, el método lanza una excepción de 
     * NullPointerException. Este constructor es sugerido desde la documentación 
     * de la clase AbstractList.
     * @param c la colección cuyos elementos serán copiados en la lista.
     * @throws NullPointerException si la referencia c es null.
     */
    public TSBArrayList(Collection<? extends E> c)
    {
        this.items = c.toArray();
        initial_capacity = c.size();
        count = c.size();
    }

    /**
     * Crea una lista con initialCapacity casilleros de capacidad, pero ninguno
     * ocupado realmente: la lista está vacía a todos los efectos prácticos. Si 
     * el valor de initialCapacity es <= 0, el valor se ajusta a 10.
     * @param initialCapacity la capacidad inicial de la lista.
     */
    public TSBArrayList(int initialCapacity)
    {
        if (initialCapacity <= 0) 
        {
            initialCapacity = 10;
        }
        items = new Object[initialCapacity];
        initial_capacity = initialCapacity;
        count = 0;
    }

    /**
     * Añade el objeto e en la posisicón index de la lista . La inserción será 
     * rechazada si la referencia e es null (nen ese caso, el método sale sin
     * hacer nada). Si index coincide con el tamaño de la lista, el objeto e 
     * será agregado exactamente al final de la lista, como si se hubiese 
     * invocado a add(e). Este método es sugerido desde la documentación de la 
     * clase AbstractList.
     * @param index el índice de la casilla donde debe quedar el objeto e.
     * @param e el objeto a agregar en la lista.
     * @throws IndexOutOfBoundsException si index < 0 o index > size().
     */
    @Override
    public void add(int index, E e)
    {        
        if(index > count || index < 0)
        {
            throw new IndexOutOfBoundsException("add(): índice fuera de rango...");
        }
        
        if(e == null) return;
        
        if(count == items.length) this.ensureCapacity(items.length * 2);
        
        int t = count - index;
        System.arraycopy(items, index, items, index+1, t);
        items[index] = e;
        count++;
        
        // detección rápida de fallas en el iterador (fail-fast iterator)...
        // modCount se hereda desde AbstractList y es protected...
        this.modCount++;  
    }   
    
    /**
     * Elimina todo el contenido de la lista, y reinicia su capacidad al valor
     * de la capacidad con que fue creada originalmente. La lista queda vacía 
     * luego de invocar a clear().
     */
    @Override
    public void clear()
    {
        items = new Object[initial_capacity];
        count = 0;
        
        // detección rápida de fallas en el iterador (fail-fast iterator)...
        // modCount se hereda desde AbstractList y es protected...
        this.modCount = 0; 
    }
    
    /**
     * Retorna una copia superficial de la lista (no se clonan los objetos que
     * la lista contiene: se retorna una lista que contiene las direcciones de
     * los mismos objetos que contiene la original).
     * @return una copia superficial de la lista.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        TSBArrayList<?> temp = (TSBArrayList<?>) super.clone();
        temp.items = new Object[count];
        System.arraycopy(this.items, 0, temp.items, 0, count);

        // detección rápida de fallas en el iterador (fail-fast iterator)...
        // modCount se hereda desde AbstractList y es protected...
        temp.modCount = 0; 

        return temp;
    }
    
    /**
     * Devuelve true si la lista contiene al elemento e. Si e es null el método
     * retorna false. Puede lanzar una excepción de ClassCastException si la clase
     * de e no es compatible con el contenido de la lista.
     * @param e el objeto a buscar en la lista.
     * @return true si la lista contiene al objeto e.
     * @throws ClassCastException si e no es compatible con los objetos de la lista.
     */
    @Override
    public boolean contains(Object e)
    {
        if(e == null) return false;
        
        for(int i=0; i<count; i++)
        {
            if(e.equals(items[i])) return true;
        }
        return false;
    }    
    
    /**
     * Aumenta la capacidad del arreglo de soporte, si es necesario, para 
     * asegurar que pueda contener al menos un número de elementos igual al 
     * indicado por el parámetro minCapacity.
     * @param minCapacity - la mínima capacidad requerida.
     */
    public void ensureCapacity(int minCapacity)
    {
        if(minCapacity == items.length) return;
        if(minCapacity < count) return;
        
        Object[] temp = new Object[minCapacity];
        System.arraycopy(items, 0, temp, 0, count);
        items = temp;
    }
    
    /**
     * Retorna el objeto contenido en la casilla index. Si el valor de index no 
     * es válido, el método lanzará una excepción de la clase
     * IndexOutOfBoundsException. Este método es sugerido desde la documentación 
     * de la clase AbstractList.
     * @param index índice de la casilla a acceder.
     * @return referencia al objeto contenido en la casilla index.
     * @throws IndexOutOfBoundsException si index < 0 o index >= size().
     */
    @Override
    public E get(int index)
    {
        if (index < 0 || index >= count)
        {
            throw new IndexOutOfBoundsException("get(): índice fuera de rango...");
        }
        return (E) items[index];
    }
    
    /**
     * Devuelve true si la lista no contiene elementos.
     * @return true si la lista está vacía.
     */
    @Override
    public boolean isEmpty()
    {
        return (count == 0);
    }   
    
    /**
     * Remueve de la lista el elemento contenido en la posición index. Los 
     * objetos ubicados a la derecha de este, se desplazan un casillero a la 
     * izquierda. El objeto removido es retornado. La capacidad de la lista no
     * se altera. Si el valor de index no es válido, el método lanzará una 
     * excepción de IndexOutOfBoundsException. Este método es sugerido desde la 
     * documentación de la clase AbstractList.
     * @param index el índice de la casilla a remover.
     * @return el objeto removido de la lista.
     * @throws IndexOutOfBoundsException si index < 0 o index >= size().
     */
    @Override
    public E remove(int index)
    {
        if(index >= count || index < 0)
        {
            throw new IndexOutOfBoundsException("remove(): índice fuera de rango...");
        }
        
        int t = items.length;
        if(count < t/2) this.ensureCapacity(t/2);
        
        Object old = items[index];
        int n = count;
        System.arraycopy(items, index+1, items, index, n-index-1);
        count--;
        items[count] = null;
        
        // detección rápida de fallas en el iterador (fail-fast iterator)...
        // modCount se hereda desde AbstractList y es protected...
        this.modCount++; 
        
        return (E) old;
    }

    /**
     * Reemplaza el objeto en la posición index por el referido por element, y
     * retorna el objeto originalmente contenido en la posición index. Si el 
     * valor de index no es válido, el método lanzará una excepción de la clase
     * IndexOutOfBoundsException. Este método es sugerido desde la documentación 
     * de la clase AbstractList.
     * @param index índice de la casilla a acceder.
     * @param element el objeto que será ubicado en la posición index.
     * @return el objeto originalmente contenido en la posición index.
     * @throws IndexOutOfBoundsException si index < 0 o index >= size().
     */
    @Override
    public E set(int index, E element)
    {
        if (index < 0 || index >= count)
        {
            throw new IndexOutOfBoundsException("set(): índice fuera de rango...");
        }
        Object old = items[index];
        items[index] = element;
        return (E) old;
    }

    /**
     * Retorna el tamaño de la lista: la cantidad de elementos realmente 
     * contenidos en ella. Este método es sugerido desde la documentación de la 
     * clase AbstractList.
     * @return la cantidad de elementos que la lista contiene.
     */
    @Override
    public int size()
    {
        return count;
    }
       
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        buff.append('{');
        for (int i=0; i<count; i++)
        {
            buff.append(items[i]);
            if(i < count-1)
            {
                buff.append(", ");
            }
        }
        buff.append('}');
        return buff.toString();
    }
    
    /**
     * Ajusta el tamaño del arreglo de soporte, para que coincida con el tamaño
     * de la lista. Puede usarse este método para que un programa ahorre un poco
     * de memoria en cuanto al uso de la lista, si es necesario.
     */
    public void trimToSize()
    {
        if(count == items.length) return;
        
        Object temp[] = new Object[count];
        System.arraycopy(items, 0, temp, 0, count);
        items = temp;
    }
}
