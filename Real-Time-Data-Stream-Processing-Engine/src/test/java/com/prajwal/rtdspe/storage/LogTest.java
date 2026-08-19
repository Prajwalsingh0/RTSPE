// package com.prajwal.rtdspe.storage;

// import com.prajwal.rtdspe.common.Record;
// import org.junit.jupiter.api.Test;

// import java.lang.reflect.Constructor;
// import java.lang.reflect.Field;
// import java.util.Arrays;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// public class LogTest {
//     @Test
//     public void appendAndRead() throws Exception {
//         Log log = new Log();
//         Record r = createRecord(new byte[]{1, 2, 3});
//         log.append(r);
//         assertEquals(1, log.size());
//         assertEquals(r, log.read(0));
//     }

//     private Record createRecord(byte[] data) throws Exception {
//         Record record = null;
//         for (Constructor<?> ctor : Record.class.getDeclaredConstructors()) {
//             ctor.setAccessible(true);
//             Object[] args = new Object[ctor.getParameterCount()];
//             Class<?>[] types = ctor.getParameterTypes();
//             for (int i = 0; i < types.length; i++) {
//                 Class<?> type = types[i];
//                 if (type == byte[].class) {
//                     args[i] = data;
//                 } else if (type == int.class) {
//                     args[i] = 0;
//                 } else if (type == long.class) {
//                     args[i] = 0L;
//                 } else if (type == short.class) {
//                     args[i] = (short) 0;
//                 } else if (type == byte.class) {
//                     args[i] = (byte) 0;
//                 } else if (type == boolean.class) {
//                     args[i] = false;
//                 } else if (type == char.class) {
//                     args[i] = '\0';
//                 } else {
//                     args[i] = null;
//                 }
//             }
//             try {
//                 record = (Record) ctor.newInstance(args);
//                 break;
//             } catch (Exception ignored) {
//             }
//         }
//         if (record == null) {
//             throw new IllegalStateException("Unable to instantiate Record");
//         }
//         Field field = Arrays.stream(Record.class.getDeclaredFields())
//                 .filter(f -> f.getType() == byte[].class)
//                 .findFirst()
//                 .orElseThrow(() -> new IllegalStateException("No byte[] field found in Record"));
//         field.setAccessible(true);
//         field.set(record, data);
//         return record;
//     }
// }
