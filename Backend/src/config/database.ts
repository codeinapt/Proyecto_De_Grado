import { Pool } from 'pg';
import dotenv from 'dotenv';

// 1. Cargamos las variables de entorno desde el archivo .env
dotenv.config();

// 2. Creamos un "Pool" de conexiones a la base de datos.
// Un pool mantiene varias conexiones abiertas y listas para ser usadas, 
// lo que es mucho más rápido que abrir y cerrar una conexión por cada petición.
const pool = new Pool({
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    host: process.env.DB_HOST,
    port: Number(process.env.DB_PORT), // Convertimos a número porque las variables del .env siempre son strings
    database: process.env.DB_NAME,
});

// 3. Vamos a añadir un evento 'connect' solo para que nos avise por consola si se conecta bien la primera vez
pool.on('connect', () => {
    console.log('✅ Conexión a la base de datos PostgreSQL establecida con éxito');
});

// 4. También capturamos errores por si la base de datos se cae o hay problemas
pool.on('error', (err) => {
    console.error('❌ Error inesperado en el pool de conexiones de PostgreSQL', err);
    process.exit(-1);
});

// 5. Exportamos el pool para que el resto de la aplicación (los repositories) pueda hacer queries
export default pool;
