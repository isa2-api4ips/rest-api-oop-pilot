const fs = require("fs");
const JsonSchema = require("@hyperjump/json-schema");

describe('Test Schema with no errors', () => {
    let schema;

    beforeEach( async () => {
        JsonSchema.setShouldMetaValidate(true);
        schema = await JsonSchema.get("file://./schemas/messaging-schema.json");
    });

    it('Should be valid', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-ok-simple.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);
        console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

});