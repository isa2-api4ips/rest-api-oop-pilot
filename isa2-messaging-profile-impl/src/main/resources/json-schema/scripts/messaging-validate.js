#!/usr/bin/env node

const fs = require("fs");
const JsonSchema = require("@hyperjump/json-schema");


(async function () {
       
       JsonSchema.setShouldMetaValidate(true);
       const schema = await JsonSchema.get("file:///cef/code/misc/OpenAPI-Specification/scripts/test/messaging-schema.json");

        // Example: Validate instance
        var data = JSON.parse(fs.readFileSync('/cef/code/misc/OpenAPI-Specification/scripts/test/messaging-example.json'));

        const output = await JsonSchema.validate(schema, data);
        if (output.valid) {
          console.log("Instance is valid!");
        } else {
          console.log("Instance is INVALID");
        }
        console.log(JSON.stringify(output, null, "  "));

        // Example: Precompile validator
//        const isString = await JsonSchema.validate(schema);
//        const output = isString("foo");

        // Example: Specify output format
       // const output = await JsonSchema.validate(schema, "foo", JsonSchema.VERBOSE);

        // Example: Specify meta-validation output format
        JsonSchema.setMetaOutputFormat(JsonSchema.FLAG);

        // Example: Disable meta-validation
        JsonSchema.setShouldMetaValidate(false);

}());
