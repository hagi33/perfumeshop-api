-- V6: pobla image_url en los perfumes existentes y añade nuevos perfumes con imagen.
-- Imágenes: Unsplash (uso libre). Las URLs usan el formato images.unsplash.com
-- con parámetros de recorte para servir una imagen cuadrada optimizada.
-- IMPORTANTE: verifica cada URL en el navegador; si alguna falla, sustitúyela.

-- 1. Actualiza los 4 perfumes del seed original (V2), que tenían image_url a NULL
UPDATE perfumes SET image_url = 'https://images.unsplash.com/photo-1594035910387-fea47794261f?w=600&h=600&fit=crop'
  WHERE name = 'Oud Wood' AND brand = 'Tom Ford';
UPDATE perfumes SET image_url = 'https://images.unsplash.com/photo-1585386959984-a4155224a1ad?w=600&h=600&fit=crop'
  WHERE name = 'La Nuit de l''Homme' AND brand = 'Yves Saint Laurent';
UPDATE perfumes SET image_url = 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600&h=600&fit=crop'
  WHERE name = 'Black Orchid' AND brand = 'Tom Ford';
UPDATE perfumes SET image_url = 'https://images.unsplash.com/photo-1547887538-e3a2f32cb1cc?w=600&h=600&fit=crop'
  WHERE name = 'Aventus' AND brand = 'Creed';

-- 2. Añade nuevos perfumes, todos con imagen
INSERT INTO perfumes (name, brand, description, family, concentration, gender, volume_ml, price, stock, image_url)
VALUES
('Khamrah', 'Lattafa', 'Cardamomo, canela y vainilla ambarada.', 'ORIENTAL', 'EAU_DE_PARFUM', 'UNISEX', 100, 35.00, 40,
  'https://images.unsplash.com/photo-1615634260167-c8cdede054de?w=600&h=600&fit=crop'),
('Fig Blossom', 'Zara', 'Higo verde, cedro y notas lácteas.', 'WOODY', 'EAU_DE_TOILETTE', 'UNISEX', 90, 22.95, 50,
  'https://images.unsplash.com/photo-1592914610354-fd354ea45e48?w=600&h=600&fit=crop'),
('Grand Soir', 'Maison Francis Kurkdjian', 'Ámbar cálido con benjuí y vainilla.', 'ORIENTAL', 'EAU_DE_PARFUM', 'UNISEX', 70, 195.00, 10,
  'https://images.unsplash.com/photo-1610461888750-10bfc601b874?w=600&h=600&fit=crop'),
('Tobacco Vanille', 'Tom Ford', 'Tabaco especiado, vainilla y frutos secos.', 'ORIENTAL', 'EAU_DE_PARFUM', 'UNISEX', 50, 260.00, 9,
  'https://images.unsplash.com/photo-1541643600914-78b084683601?w=600&h=600&fit=crop'),
('Bade''e Al Oud', 'Lattafa', 'Oud intenso con rosa y azafrán.', 'WOODY', 'EAU_DE_PARFUM', 'UNISEX', 100, 38.00, 35,
  'https://images.unsplash.com/photo-1588405748880-12d1d2a59d75?w=600&h=600&fit=crop'),
('Y Le Parfum', 'Yves Saint Laurent', 'Lavanda, salvia y cedro ambarado.', 'AROMATIC', 'EAU_DE_PARFUM', 'MASCULINE', 100, 105.00, 20,
  'https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=600&h=600&fit=crop'),
('Angels'' Share', 'By Kilian', 'Coñac, canela y vainilla tostada.', 'GOURMAND', 'EAU_DE_PARFUM', 'UNISEX', 50, 240.00, 7,
  'https://images.unsplash.com/photo-1557170334-a9632e77c6e4?w=600&h=600&fit=crop'),
('Erba Pura', 'Xerjoff', 'Frutos cítricos, ámbar y almizcle blanco.', 'CITRUS', 'EAU_DE_PARFUM', 'UNISEX', 100, 185.00, 12,
  'https://images.unsplash.com/photo-1619994403073-2cec844b8e63?w=600&h=600&fit=crop');