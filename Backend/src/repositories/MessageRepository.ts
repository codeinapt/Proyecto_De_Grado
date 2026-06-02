import pool from '../config/database';
import { Message, EstadoMensaje } from '../models/MessageModel';

export class MessageRepository {
    
    // Método para insertar un lote (array) de mensajes en la base de datos
    async saveBatch(messages: Message[]): Promise<number> {
        
        // Si el arreglo viene vacío, no hacemos nada y devolvemos 0
        if (messages.length === 0) return 0;

        // Obtenemos una conexión exclusiva (cliente) del pool para hacer una "Transacción"
        const client = await pool.connect();
        
        let insertedCount = 0;

        try {
            // 1. Iniciamos la transacción (Si algo falla, echamos todo para atrás)
            await client.query('BEGIN');

            const query = `
                INSERT INTO mensaje (uuid_mensaje, emisor_id, receptor_id, contenido_cifrado, tipo_mensaje, estado, contador_saltos, fecha_emision_movil, fecha_servidor)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
                ON CONFLICT (uuid_mensaje) DO NOTHING;
            `;

            // 2. Recorremos el arreglo y ejecutamos el INSERT por cada mensaje
            for (const msg of messages) {
                const values = [
                    msg.uuid_mensaje,
                    msg.emisor_id,
                    msg.receptor_id,
                    msg.contenido_cifrado,
                    msg.tipo_mensaje || 'TEXTO',
                    msg.estado || 'EN_ESPERA',
                    msg.contador_saltos || 0,
                    msg.fecha_emision_movil,
                    msg.fecha_servidor || new Date()
                ];

                const result = await client.query(query, values);
                
                // Si rowCount es 1, significa que se insertó. Si es 0, es que ya existía (gracias al DO NOTHING)
                if (result.rowCount && result.rowCount > 0) {
                    insertedCount++;
                }
            }

            // 3. Confirmamos y guardamos todos los cambios en la BD definitivamente
            await client.query('COMMIT');
            
            return insertedCount;

        } catch (error) {
            // 4. Si ocurrió CUALQUIER error en medio del proceso, cancelamos TODO (Rollback)
            await client.query('ROLLBACK');
            throw error; // Relanzamos el error para que el controlador lo atrape
        } finally {
            // 5. Devolvemos la conexión al pool
            client.release();
        }
    }

    async getPendingMessages(): Promise<Message[]> {
        const query = `
            SELECT *
            FROM mensaje
            WHERE estado = $1
            ORDER BY fecha_servidor ASC
        `;

        const result = await pool.query(query, [EstadoMensaje.RECIBIDO_EN_NUBE]);
        return result.rows;
    }

    async getAndMarkPendingMessages(): Promise<Message[]> {
        const query = `
            UPDATE mensaje
            SET estado = $1
            WHERE uuid_mensaje IN (
                SELECT uuid_mensaje
                FROM mensaje
                WHERE estado = $2
                ORDER BY fecha_servidor ASC
            )
            RETURNING *;
        `;

        const result = await pool.query(query, [
            EstadoMensaje.TRANSITANDO,
            EstadoMensaje.RECIBIDO_EN_NUBE
        ]);

        return result.rows;
    }

    async acknowledgeMessage(uuid_mensaje: string, receptor_id: string): Promise<Message> {
        const query = `
            UPDATE mensaje
            SET estado = $1
            WHERE uuid_mensaje = $2 AND receptor_id = $3
            RETURNING *;
        `;

        const values = [EstadoMensaje.ENTREGADO, uuid_mensaje, receptor_id];
        const result = await pool.query(query, values);

        if (result.rows.length === 0) {
            throw new Error('No se encontró el mensaje para ACK o el receptor no coincide');
        }

        return result.rows[0];
    }
}
