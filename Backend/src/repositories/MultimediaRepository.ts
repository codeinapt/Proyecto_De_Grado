import pool from '../config/database';
import { Multimedia } from '../models/MultimediaModel';

export class MultimediaRepository {
    async messageExists(mensaje_id: string): Promise<boolean> {
        const query = 'SELECT 1 FROM mensaje WHERE uuid_mensaje = $1 LIMIT 1';
        const result = await pool.query(query, [mensaje_id]);
        return result.rows.length > 0;
    }

    async create(multimedia: Multimedia): Promise<Multimedia> {
        const query = `
            INSERT INTO multimedia (mensaje_id, url_almacenamiento, peso_bytes, thumbnail_base64)
            VALUES ($1, $2, $3, $4)
            RETURNING *;
        `;

        const values = [
            multimedia.mensaje_id,
            multimedia.url_almacenamiento,
            multimedia.peso_bytes,
            multimedia.thumbnail_base64 || null
        ];

        const result = await pool.query(query, values);
        return result.rows[0];
    }
}
