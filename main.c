struct ExampleStructure{
    uint8_t enable; /* min: 0, max: 1 */
    int16_t coefTable[3U]; /* min: -500, max: 500 */
    uint8_t edgeMin; /* min: -100, max: 100 */
    uint8_t edgeMax; /* min: -100, max: 100 */
};
