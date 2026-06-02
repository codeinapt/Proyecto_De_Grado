import pool from '../config/database';
import { User } from '../models/UserModel';

export class UserRepository {

    // Método para crear un usuario en la BD
    async createUser(user: User): Promise<User> {
        // La consulta SQL. $1 y $2 son los valores dinámicos
        const query = `
            INSERT INTO usuario (nombre_usuario, public_key)
            VALUES ($1, $2)
            RETURNING *;
        `;

        // Los valores que van a reemplazar a $1 y $2
        const values = [user.nombre_usuario, user.public_key];

        // Ejecutamos la consulta
        const result = await pool.query(query, values);

        // Devolvemos el primer (y único) resultado insertado
        return result.rows[0];
    }

    // Método para buscar si un usuario ya existe por su nombre
    async findByUsername(nombre_usuario: string): Promise<User | null> {
        const query = 'SELECT * FROM usuario WHERE nombre_usuario = $1';

        const result = await pool.query(query, [nombre_usuario]);

        // Si la BD devolvió resultados, devolvemos el primero. Si no, devolvemos null.
        return result.rows.length > 0 ? result.rows[0] : null;
    }
}
