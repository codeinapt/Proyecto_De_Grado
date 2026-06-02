import { Request, Response } from 'express';
import { MultimediaService } from '../services/MultimediaService';

export class MultimediaController {
    private multimediaService: MultimediaService;

    constructor() {
        this.multimediaService = new MultimediaService();
    }

    async uploadEncrypted(req: Request, res: Response): Promise<void> {
        try {
            const mensaje_id = req.header('x-mensaje-id');
            const thumbnail_base64 = req.header('x-thumbnail-base64');
            const fileBuffer = req.body as Buffer;

            if (!mensaje_id) {
                res.status(400).json({ error: 'El header x-mensaje-id es obligatorio' });
                return;
            }

            const multimedia = await this.multimediaService.saveEncryptedFile(
                mensaje_id,
                fileBuffer,
                thumbnail_base64
            );

            res.status(201).json({
                message: 'Archivo cifrado almacenado correctamente',
                multimedia
            });
        } catch (error: any) {
            res.status(400).json({ error: error.message });
        }
    }
}
