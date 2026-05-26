import { Request, Response } from 'express';
import { MessageService } from '../services/MessageService';

export class MessageController {
    private messageService: MessageService;

    constructor() {
        this.messageService = new MessageService();
    }

    async ack(req: Request, res: Response): Promise<void> {
        try {
            const { uuid_mensaje, receptor_id } = req.body;

            if (!uuid_mensaje || !receptor_id) {
                res.status(400).json({ error: 'uuid_mensaje y receptor_id son obligatorios' });
                return;
            }

            const mensajeActualizado = await this.messageService.acknowledgeMessage(uuid_mensaje, receptor_id);

            res.status(200).json({
                message: 'ACK recibido correctamente. Mensaje marcado como ENTREGADO',
                mensaje: mensajeActualizado
            });
        } catch (error: any) {
            res.status(400).json({ error: error.message });
        }
    }
}
