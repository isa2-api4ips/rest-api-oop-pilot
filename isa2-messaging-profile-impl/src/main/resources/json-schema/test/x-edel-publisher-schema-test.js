const fs = require("fs");
const JsonSchema = require("@hyperjump/json-schema");

describe('Testing path', () => {
    let schema;

    beforeEach( async () => {
        JsonSchema.setShouldMetaValidate(true);
        schema = await JsonSchema.get("file://./schemas/messaging-schema.json");
    });

    it('Invalid path: missing service/action', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-missing-x-edel-lifecycle.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

       // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(false);
        expect(output.errors.length).not.toBe(0);
        expect(output.errors).toEqual(
            expect.arrayContaining([
                expect.objectContaining({
                    keyword: 'https://json-schema.org/draft/2020-12/schema#required',
                    instanceLocation: '#/info'
                })
            ])
        )

    });
});